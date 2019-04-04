package com.gzdzsss.authserver.endpoint;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.endpoint.FrameworkEndpoint;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.View;
import org.springframework.web.util.HtmlUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;
import java.util.Map;

/**
 * @author <a href="mailto:zhouyanjie666666@gmail.com">zyj</a>
 * @date 2019/4/2
 */

@FrameworkEndpoint
public class UserEndpoint {

    @Autowired
    private UserDetailsManager userDetailsManager;

    @Autowired
    private PasswordEncoder passwordEncoder;


    @RequestMapping(value = "/user/register", method = RequestMethod.GET)
    public ModelAndView register(Map<String, Object> model, HttpServletRequest request) {
        CsrfToken csrfToken = (CsrfToken) (model.containsKey("_csrf") ? model.get("_csrf") : request.getAttribute("_csrf"));
        return msgModelAndView(null, csrfToken, request.getContextPath());
    }


    @RequestMapping(method = RequestMethod.POST, value = "/user/register")
    public ModelAndView register(String username,
                                 String password,
                                 Map<String, Object> model, HttpServletRequest request) {

        CsrfToken csrfToken = (CsrfToken) (model.containsKey("_csrf") ? model.get("_csrf") : request.getAttribute("_csrf"));
        if (username == null || password == null
                || username.length() < 4 || username.length() > 20
                || password.length() < 6 || password.length() > 20) {
            return msgModelAndView("用户名长度为4-20， 密码长度为6-20", csrfToken, request.getContextPath());
        }

        if (userDetailsManager.userExists(username)) {
            return msgModelAndView("用户名已存在", csrfToken, request.getContextPath());
        }

        List<GrantedAuthority> authorities = AuthorityUtils.createAuthorityList("USER");
        UserDetails userDetails = new User(username, passwordEncoder.encode(password), authorities);
        userDetailsManager.createUser(userDetails);
        return getModelAndView("注册成功");
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


    private String createTemplate(String msg, CsrfToken csrfToken, String contextPath) {
        StringBuilder builder = new StringBuilder();
        builder.append("<html><body><h1>register</h1>");
        if (!StringUtils.isEmpty(msg)) {
            builder.append("<p style=\"color: red;\" >").append(msg).append("</p>");
        }
        builder.append("<form id=\"confirmationForm\" name=\"confirmationForm\" method=\"post\"  action=\"");
        builder.append(contextPath).append("/user/register\">");
        builder.append("<label>username</label><input name=\"username\"   type=\"text\"/><br>");
        builder.append("<label>password</label><input name=\"password\"   type=\"password\"/><br>");

        if (csrfToken != null) {
            builder.append("<input type=\"hidden\" name=\"" + HtmlUtils.htmlEscape(csrfToken.getParameterName()) +
                    "\" value=\"" + HtmlUtils.htmlEscape(csrfToken.getToken()) + "\" />");
        }


        builder.append("<label><input name=\"register\" value=\"register\" type=\"submit\"/></label>");
        builder.append("</form> ");
        builder.append("</body></html>");
        return builder.toString();
    }


}
