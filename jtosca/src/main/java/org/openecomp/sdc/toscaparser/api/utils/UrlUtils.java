package org.openecomp.sdc.toscaparser.api.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.openecomp.sdc.toscaparser.api.common.ExceptionCollector;

public class UrlUtils {
	
	public static boolean validateUrl(String sUrl) {
        // Validates whether the given path is a URL or not

        // If the given path includes a scheme (http, https, ftp, ...) and a net
        // location (a domain name such as www.github.com) it is validated as a URL
		try {
			URL url = new URL(sUrl);
			if(url.getProtocol().equals("file")) {
				return true;
			}
			return url.getAuthority() != null;
		}
		catch(MalformedURLException e) {
			return false; 
		}
	}
	
	public static String joinUrl(String sUrl,String relativePath) {
        // Builds a new URL from the given URL and the relative path

        // Example:
        //   url: http://www.githib.com/openstack/heat
        //   relative_path: heat-translator
        //   - joined: http://www.githib.com/openstack/heat-translator
		if(!validateUrl(sUrl)) {
			ThreadLocalsHolder.getCollector().appendException(String.format(
					"ValueError: The URL \"%s\" is malformed",sUrl));
		}
		try {
			URL base = new URL(sUrl);
			return (new URL(base,relativePath)).toString();
		}
		catch(MalformedURLException e) {
			ThreadLocalsHolder.getCollector().appendException(String.format(
					"ValueError: Joining URL \"%s\" and relative path \"%s\" caused an exception",sUrl,relativePath));
			return sUrl; 
		}
	}

	public static boolean isUrlAccessible(String sUrl) {
        // Validates whether the given URL is accessible

        // Returns true if the get call returns a 200 response code.
        // Otherwise, returns false.
		try {
			HttpURLConnection connection = (HttpURLConnection) new URL(sUrl).openConnection();
			connection.setRequestMethod("HEAD");
			int responseCode = connection.getResponseCode();
			return responseCode == 200;
		}
		catch(IOException e) {
			return false;
		}
	}

}

/*python

from six.moves.urllib.parse import urljoin
from six.moves.urllib.parse import urlparse
from toscaparser.common.exception import ExceptionCollector
from toscaparser.utils.gettextutils import _

try:
    # Python 3.x
    import urllib.request as urllib2
except ImportError:
    # Python 2.x
    import urllib2


class UrlUtils(object):

    @staticmethod
    def validate_url(path):
        """Validates whether the given path is a URL or not.

        If the given path includes a scheme (http, https, ftp, ...) and a net
        location (a domain name such as www.github.com) it is validated as a
        URL.
        """
        parsed = urlparse(path)
        if parsed.scheme == 'file':
            # If the url uses the file scheme netloc will be ""
            return True
        else:
            return bool(parsed.scheme) and bool(parsed.netloc)

    @staticmethod
    def join_url(url, relative_path):
        """Builds a new URL from the given URL and the relative path.

        Example:
          url: http://www.githib.com/openstack/heat
          relative_path: heat-translator
          - joined: http://www.githib.com/openstack/heat-translator
        """
        if not UrlUtils.validate_url(url):
            ExceptionCollector.appendException(
                ValueError(_('"%s" is not a valid URL.') % url))
        return urljoin(url, relative_path)

    @staticmethod
    def url_accessible(url):
        """Validates whether the given URL is accessible.

        Returns true if the get call returns a 200 response code.
        Otherwise, returns false.
        """
        return urllib2.urlopen(url).getcode() == 200
*/