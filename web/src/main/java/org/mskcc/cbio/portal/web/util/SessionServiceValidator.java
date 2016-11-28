/*
 * Copyright (c) 2015 Memorial Sloan-Kettering Cancer Center.
 *
 * This library is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY, WITHOUT EVEN THE IMPLIED WARRANTY OF MERCHANTABILITY OR FITNESS
 * FOR A PARTICULAR PURPOSE. The software and documentation provided hereunder
 * is on an "as is" basis, and Memorial Sloan-Kettering Cancer Center has no
 * obligations to provide maintenance, support, updates, enhancements or
 * modifications. In no event shall Memorial Sloan-Kettering Cancer Center be
 * liable to any party for direct, indirect, special, incidental or
 * consequential damages, including lost profits, arising out of the use of this
 * software and its documentation, even if Memorial Sloan-Kettering Cancer
 * Center has been advised of the possibility of such damage.
 */

/*
 * This file is part of cBioPortal.
 *
 * cBioPortal is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
*/

package org.mskcc.cbio.portal.web.util;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.json.simple.JSONObject;

/**
 *
 * @author Manda Wilson
 */
public class SessionServiceValidator {

  private static int MAX_LENGTH = 10000;
  private static int MAX_SESSION_SERVICE_REQ = 10;
  private static String NUM_SESSION_SERVICE_REQ = "count_session_requests";

  private static Log LOG = LogFactory.getLog(SessionServiceValidator.class);

  public static boolean isValid(JSONObject session, HttpServletRequest request) {
    if (session.isEmpty()) {
      LOG.warn("SessionServiceValidator.isValid() -- session is empty");
      return false;
    } 

    String sessionString = session.toString();
    int sessionLength = sessionString.length();
    if (MAX_LENGTH < sessionLength) {
      LOG.warn("SessionServiceValidator.isValid() -- session is too long, length is = " + 
        sessionLength + " (MAX_LENGTH = " + MAX_LENGTH + ")");
      return false;
    }

    Integer countRequests = (Integer) request.getSession().getAttribute(NUM_SESSION_SERVICE_REQ);
    int count = 0;
    // do this last after we have checked this is valid
    if (countRequests == null) {
      count = 1; 
    } else {
      count = countRequests.intValue();   
      count += 1;
    }
    LOG.debug("SessionServiceValidator.isValid() -- " + count + 
      " session service API requests made by this session (MAX_SESSION_SERVICE_REQ = " + 
      MAX_SESSION_SERVICE_REQ + ")");
    request.getSession().setAttribute(NUM_SESSION_SERVICE_REQ, new Integer(count));
    if (MAX_SESSION_SERVICE_REQ < count) {
      LOG.warn("SessionServiceValidator.isValid() -- too many requests (" + count + 
        ") made by this session to the session service API (MAX_SESSION_SERVICE_REQ = " + 
        MAX_SESSION_SERVICE_REQ + ")");
      return false;
    }

    return true;
  }

}
