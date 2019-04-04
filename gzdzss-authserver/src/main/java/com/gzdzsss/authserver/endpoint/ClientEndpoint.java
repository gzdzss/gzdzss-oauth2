package com.gzdzsss.authserver.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail">zyj</a>
 * @date 2019/4/2
 */

@FrameworkEndpoint
@SessionAttributes("authorizationRequest")
public class ClientEndpoint {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(value = "/client/list", method = RequestMethod.GET)
    public ModelAndView list(Authentication authentication, HttpServletRequest request) {
        List<String> clientIds = jdbcTemplate.queryForList("SELECT client_id FROM `oauth_client_details` where username = ?", String.class, authentication.getName());
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><h1>OAuth Apps</h1>");
        builder.append("<p>").append(authentication.getName()).append("</p>");
        builder.append("<a href=\"").append(request.getContextPath()).append("/client/register\">New Oauth App</a>");
        builder.append("<hr>");
        if (CollectionUtils.isEmpty(clientIds)) {
            builder.append("<p>none</p>");
        } else {
            for (String clientId : clientIds) {
                builder.append("<p><a href=\"").append(request.getContextPath()).append("/client/").append(clientId).append(" \">");
                builder.append(clientId);
                builder.append("</a></p>");
            }
        }
        builder.append("</body></html>");
        View view = new View() {
            @Override
            public String getContentType() {
                return "text/html;charset=utf-8";
            }

            @Override
            public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                response.setContentType(getContentType());
                response.getWriter().append(builder.toString());
            }
        };
        return new ModelAndView(view);
    }


    @RequestMapping(value = "/client/{clientId}", method = RequestMethod.GET)
    public ModelAndView clients(@PathVariable String clientId, Authentication authentication) {
        List<Map<String, Object>> clientDetails = jdbcTemplate.queryForList("select  client_id , client_secret, web_server_redirect_uri from oauth_client_details  where client_id = ? and username = ?", clientId, authentication.getName());
        StringBuilder builder = new StringBuilder();
        if (CollectionUtils.isEmpty(clientDetails)) {
            builder.append("<html><body><h1>Hey, where are you going?</h1></body></html>");
        } else {
            builder.append("<html><body>");
            builder.append("<p>clientId:").append(clientDetails.get(0).get("client_id")).append("</p>");
            builder.append("<p>redirectUri:").append(clientDetails.get(0).get("web_server_redirect_uri")).append("</p>");
            builder.append("</body></html>");
        }
        View view = new View() {
            @Override
            public String getContentType() {
                return "text/html;charset=utf-8";
            }

            @Override
            public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                response.setContentType(getContentType());
                response.getWriter().append(builder.toString());
            }
        };
        return new ModelAndView(view);
    }


    @RequestMapping(value = "/client/register", method = RequestMethod.GET)
    public ModelAndView register(Map<String, Object> model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) (model.containsKey("_csrf") ? model.get("_csrf") : request.getAttribute("_csrf"));
        return msgModelAndView(null, csrfToken, request.getContextPath());
    }


    protected String createTemplate(String msg, CsrfToken csrfToken, String contextPath) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><h1>Register a new OAuth application</h1>");
        if (!StringUtils.isEmpty(msg)) {
            builder.append("<p style=\"color: red;\" >").append(msg).append("</p>");
        }
        builder.append("<form id=\"confirmationForm\" name=\"confirmationForm\" method=\"post\"  action=\"");
        builder.append(contextPath).append("/client/register\"> ");
        builder.append("<label>client_id</label><input name=\"clientId\"   type=\"text\"/> <br>");
        builder.append("<label>client_secret</label><input name=\"clientSecret\"   type=\"text\"/><br>");
        builder.append("<label>callback_url</label><input name=\"callbackUrl\"   type=\"text\"/><br>");
        if (csrfToken != null) {
            builder.append("<input type=\"hidden\" name=\"" + HtmlUtils.htmlEscape(csrfToken.getParameterName()) +
                    "\" value=\"" + HtmlUtils.htmlEscape(csrfToken.getToken()) + "\" />");
        }
        builder.append("<label><input name=\"register\" value=\"register\" type=\"submit\"/></label>");
        builder.append("</form> ");
        builder.append("</body></html>");

        return builder.toString();
    }


    @RequestMapping(value = "/client/register", method = RequestMethod.POST)
    public ModelAndView register(Authentication authentication, String clientId, String clientSecret, String callbackUrl, Map<String, ?> model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) (model.containsKey("_csrf") ? model.get("_csrf") : request.getAttribute("_csrf"));
        if (StringUtils.isEmpty(clientId) || clientId.length() < 5 || clientId.length() > 30 ||
                StringUtils.isEmpty(clientSecret) || clientSecret.length() < 5 || clientSecret.length() > 30 ||
                StringUtils.isEmpty(callbackUrl)) {
            return msgModelAndView("clientId（5-30位）,clientSecret(5-30位),callbackUrl不能为空", csrfToken, request.getContextPath());
        }


        int count = jdbcTemplate.queryForObject("SELECT count(1) FROM `oauth_client_details` where client_id = ?", int.class, clientId);

        if (count > 0) {
            return msgModelAndView("clientId: " + clientId + "已经被占用", csrfToken, request.getContextPath());
        }

        String pwd = passwordEncoder.encode(clientSecret);
        jdbcTemplate.update("insert into oauth_client_details (client_id,client_secret,web_server_redirect_uri,scope,authorized_grant_types,username) values(?,?,?,?,?,?)", clientId, pwd, callbackUrl, "login,userinfo", "authorization_code,refresh_token,password,implicit,client_credentials", authentication.getName());


        return new ModelAndView("redirect:/client/list");
    }


    private ModelAndView msgModelAndView(String msg, CsrfToken csrfToken, String contextPath) {
        String content = createTemplate(msg, csrfToken, contextPath);
        return getModelAndView(content);
    }


    private ModelAndView getModelAndView(String content) {
        View view = new View() {
            @Override
            public String getContentType() {
                return "text/html;charset=utf-8";
            }

            @Override
            public void render(Map<String, ?> model, HttpServletRequest request, HttpServletResponse response) throws Exception {
                response.setContentType(getContentType());
                response.getWriter().append(content);
            }
        };
        return new ModelAndView(view);
    }


}
