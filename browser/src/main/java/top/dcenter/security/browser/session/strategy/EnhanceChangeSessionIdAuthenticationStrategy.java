package top.dcenter.security.browser.session.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;
import top.dcenter.security.browser.api.session.SessionEnhanceCheckService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

/**
 * A base class for performing session fixation protection.
 * 增加机器特征的校验
 * @author Rob Winch
 * @since 3.2
 * @modifiedBy zyw
 * @version V1.0  Created by 2020/6/1 22:57
 */
@Slf4j
public class EnhanceChangeSessionIdAuthenticationStrategy implements SessionAuthenticationStrategy, ApplicationEventPublisherAware {

    private SessionEnhanceCheckService sessionEnhanceCheckService;

    /**
     * Used for publishing events related to session fixation protection, such as
     * {@link SessionFixationProtectionEvent}.
     */
    private ApplicationEventPublisher applicationEventPublisher = new NullEventPublisher();
    /**
     * If set to {@code true}, a session will always be created, even if one didn't exist
     * at the start of the request. Defaults to {@code false}.
     */
    private boolean alwaysCreateSession;

    /**
     * @see ChangeSessionIdAuthenticationStrategy
     * @param sessionEnhanceCheckService    可以为 null
     */
    public EnhanceChangeSessionIdAuthenticationStrategy(SessionEnhanceCheckService sessionEnhanceCheckService) {
        this.sessionEnhanceCheckService = sessionEnhanceCheckService;
    }

    /**
     * Called when a user is newly authenticated.
     * <p>
     * If a session already exists, and matches the session Id from the client, a new
     * session will be created, and the session attributes copied to it (if
     * {@code migrateSessionAttributes} is set). If the client's requested session Id is
     * invalid, nothing will be done, since there is no need to change the session Id if
     * it doesn't match the current session.
     * <p>
     * If there is no session, no action is taken unless the {@code alwaysCreateSession}
     * property is set, in which case a session will be created if one doesn't already
     * exist.
     */
    @Override
    public void onAuthentication(Authentication authentication,
                                 HttpServletRequest request, HttpServletResponse response) {
        boolean hadSessionAlready = request.getSession(false) != null;

        if (!hadSessionAlready && !alwaysCreateSession) {
            // Session fixation isn't a problem if there's no session

            return;
        }

        // Create new session if necessary
        HttpSession session = request.getSession();

        if (hadSessionAlready && request.isRequestedSessionIdValid()) {

            String originalSessionId;
            String newSessionId;
            Object mutex = WebUtils.getSessionMutex(session);
            synchronized (mutex) {
                // We need to migrate to a new session
                originalSessionId = session.getId();

                session = applySessionFixation(request);
                newSessionId = session.getId();
            }

            if (originalSessionId.equals(newSessionId)) {
                log.warn("Your servlet container did not change the session ID when a new session was created. You will"
                                    + " not be adequately protected against session-fixation attacks");
            }

            onSessionChange(originalSessionId, session, authentication);

            if (this.sessionEnhanceCheckService != null)
            {
                this.sessionEnhanceCheckService.setEnhanceCheckValue(session, request);
            }
        }
    }

    /**
     * Applies session fixation
     *
     * @param request the {@link HttpServletRequest} to apply session fixation protection
     * for
     * @return the new {@link HttpSession} to use. Cannot be null.
     */
    HttpSession applySessionFixation(HttpServletRequest request) {
        request.changeSessionId();
        return request.getSession();
    }

    /**
     * Called when the session has been changed and the old attributes have been migrated
     * to the new session. Only called if a session existed to start with. Allows
     * subclasses to plug in additional behaviour. *
     * <p>
     * The default implementation of this method publishes a
     * {@link SessionFixationProtectionEvent} to notify the application that the session
     * ID has changed. If you override this method and still wish these events to be
     * published, you should call {@code super.onSessionChange()} within your overriding
     * method.
     *
     * @param originalSessionId the original session identifier
     * @param newSession the newly created session
     * @param auth the token for the newly authenticated principal
     */
    protected void onSessionChange(String originalSessionId, HttpSession newSession,
                                   Authentication auth) {
        applicationEventPublisher.publishEvent(new SessionFixationProtectionEvent(auth,
                                                                                  originalSessionId, newSession.getId()));
    }

    /**
     * Sets the {@link ApplicationEventPublisher} to use for submitting
     * {@link SessionFixationProtectionEvent}. The default is to not submit the
     * {@link SessionFixationProtectionEvent}.
     *
     * @param applicationEventPublisher the {@link ApplicationEventPublisher}. Cannot be
     * null.
     */
    @Override
    public void setApplicationEventPublisher(
            ApplicationEventPublisher applicationEventPublisher) {
        Assert.notNull(applicationEventPublisher,
                       "applicationEventPublisher cannot be null");
        this.applicationEventPublisher = applicationEventPublisher;
    }

    public void setAlwaysCreateSession(boolean alwaysCreateSession) {
        this.alwaysCreateSession = alwaysCreateSession;
    }

    protected static final class NullEventPublisher implements ApplicationEventPublisher {
        @Override
        public void publishEvent(ApplicationEvent event) {
        }

        @Override
        public void publishEvent(Object event) {
        }
    }

}