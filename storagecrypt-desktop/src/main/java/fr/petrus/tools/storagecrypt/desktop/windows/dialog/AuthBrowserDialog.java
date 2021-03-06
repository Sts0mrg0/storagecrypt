/*
 *  Copyright Pierre Sagne (12 december 2014)
 *
 * petrus.dev.fr@gmail.com
 *
 * This software is a computer program whose purpose is to encrypt and
 * synchronize files on the cloud.
 *
 * This software is governed by the CeCILL license under French law and
 * abiding by the rules of distribution of free software.  You can  use,
 * modify and/ or redistribute the software under the terms of the CeCILL
 * license as circulated by CEA, CNRS and INRIA at the following URL
 * "http://www.cecill.info".
 *
 * As a counterpart to the access to the source code and  rights to copy,
 * modify and redistribute granted by the license, users are provided only
 * with a limited warranty  and the software's author,  the holder of the
 * economic rights,  and the successive licensors  have only  limited
 * liability.
 *
 * In this respect, the user's attention is drawn to the risks associated
 * with loading,  using,  modifying and/or developing or reproducing the
 * software by the user in light of its specific status of free software,
 * that may mean  that it is complicated to manipulate,  and  that  also
 * therefore means  that it is reserved for developers  and  experienced
 * professionals having in-depth computer knowledge. Users are therefore
 * encouraged to load and test the software's suitability as regards their
 * requirements in conditions enabling the security of their systems and/or
 * data to be ensured and,  more generally, to use and operate it in the
 * same conditions as regards security.
 *
 * The fact that you are presently reading this means that you have had
 * knowledge of the CeCILL license and that you accept its terms.
 *
 */

package fr.petrus.tools.storagecrypt.desktop.windows.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.browser.Browser;
import org.eclipse.swt.browser.LocationAdapter;
import org.eclipse.swt.browser.LocationEvent;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fr.petrus.lib.core.cloud.RemoteStorage;
import fr.petrus.lib.core.cloud.exceptions.RemoteException;
import fr.petrus.tools.storagecrypt.desktop.TextBundle;
import fr.petrus.tools.storagecrypt.desktop.windows.AppWindow;

/**
 * A {@code Dialog} containing a {@link Browser} to let the user authenticate on the OAuth2 service
 * for remote storage.
 *
 * @author Pierre Sagne
 * @since 28.07.2015
 */
public class AuthBrowserDialog extends Dialog {

    private static Logger LOG = LoggerFactory.getLogger(AuthBrowserDialog.class);

    private AppWindow appWindow = null;
    private TextBundle textBundle = null;
    private RemoteStorage remoteStorage = null;
    private boolean success = false;
    private HashMap<String, String> responseParameters = null;

    /**
     * Creates a new {@code AuthBrowserDialog} to connect the given {@code remoteStorage}.
     *
     * @param appWindow     the application window
     * @param remoteStorage the remote storage storage to connect to
     */
    public AuthBrowserDialog(AppWindow appWindow, RemoteStorage remoteStorage) {
        super(appWindow);
        setShellStyle(SWT.TITLE | SWT.CLOSE | SWT.RESIZE);
        this.appWindow = appWindow;
        this.textBundle = appWindow.getTextBundle();
        this.remoteStorage = remoteStorage;
    }

    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(textBundle.getString("cloud_auth_dialog_title"));
        Rectangle displayBounds = newShell.getDisplay().getBounds();
        newShell.setMinimumSize(displayBounds.width/2, displayBounds.height *3/4);
    }

    /**
     * Returns whether the connection was successful.
     *
     * @return if the connection was successful
     */
    public boolean isSuccess() {
        return success;
    }

    /**
     * Returns the OAuth2 response parameters in a Map.
     *
     * @return the OAuth2 response parameters
     */
    public Map<String, String> getResponseParameters() {
        return responseParameters;
    }

    @Override
    protected Control createContents(Composite parent) {
        parent.setLayout(new FillLayout());
        success = false;
        responseParameters = null;
        try {
            final Browser browser = new Browser(parent, SWT.NONE);
            try {
                final String redirectUri = remoteStorage.oauthAuthorizeRedirectUri();
                browser.setUrl(remoteStorage.oauthAuthorizeUrl(false));
                browser.addLocationListener(new LocationAdapter() {
                    @Override
                    public void changing(LocationEvent locationEvent) {
                        if (null != locationEvent.location && locationEvent.location.startsWith(redirectUri)) {
                            try {
                                Map<String, List<String>> params = getUrlParameters(locationEvent.location);
                                responseParameters = new HashMap<>();
                                for (String parameterName : params.keySet()) {
                                    responseParameters.put(parameterName, params.get(parameterName).get(0));
                                }
                                if (!responseParameters.containsKey("error")) {
                                    success = true;
                                }
                            } catch (UnsupportedEncodingException e) {
                                LOG.error("Url parameters cannot be parsed.", e);
                            } finally {
                                // cancel the redirect to the redirectionUri
                                locationEvent.doit = false;
                                // then close the dialog
                                appWindow.asyncExec(new Runnable() {
                                    @Override
                                    public void run() {
                                        close();
                                    }
                                });
                            }
                        }
                    }
                });
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return browser;
        } catch (SWTError e) {
            LOG.error("Browser cannot be initialized.", e);
            appWindow.showErrorMessage(textBundle.getString("error_message_browser_init_error"));
        }
        return null;
    }

    private static Map<String, List<String>> getUrlParameters(String url) throws UnsupportedEncodingException {
        Map<String, List<String>> params = new HashMap<>();
        String[] urlParts = url.split("\\?");
        if (urlParts.length > 1) {
            String query = urlParts[1];
            for (String param : query.split("&")) {
                String pair[] = param.split("=");
                String key = URLDecoder.decode(pair[0], "UTF-8");
                String value = "";
                if (pair.length > 1) {
                    value = URLDecoder.decode(pair[1], "UTF-8");
                }
                List<String> values = params.get(key);
                if (values == null) {
                    values = new ArrayList<>();
                    params.put(key, values);
                }
                values.add(value);
            }
        }
        return params;
    }
}
