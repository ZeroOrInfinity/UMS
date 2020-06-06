package top.dcenter.security.core.auth.session.strategy;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.ApplicationEventPublisherAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy;
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationException;
import org.springframework.security.web.authentication.session.SessionFixationProtectionEvent;
import org.springframework.util.Assert;
import org.springframework.web.util.WebUtils;
import top.dcenter.security.core.api.session.SessionEnhanceCheckService;
import top.dcenter.security.core.properties.ClientProperties;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Comparator;
import java.util.List;

/**
 * 组合了 {@link ConcurrentSessionControlAuthenticationStrategy} 与 {@link ChangeSessionIdAuthenticationStrategy}.<br>
 * 增加机器特征的校验,
 * @see ConcurrentSessionControlAuthenticationStrategy
 * @see ChangeSessionIdAuthenticationStrategy
 * @author zyw
 * @version V1.0  Created by 2020/6/4 9:19
 */
@Slf4j
public class EnhanceConcurrentControlAuthenticationStrategy extends ConcurrentSessionControlAuthenticationStrategy implements ApplicationEventPublisherAware {

    private SessionEnhanceCheckService sessionEnhanceCheckService;

    /**
     * Used for publishing events related to session fixation protection, such as
     * {@link SessionFixationProtectionEvent}.
     */
    private ApplicationEventPublisher applicationEventPublisher = new EnhanceConcurrentControlAuthenticationStrategy.NullEventPublisher();
    private boolean exceptionIfMaximumExceeded = false;
    private int maximumSessions = 1;
    private final SessionRegistry sessionRegistry;

    private final ClientProperties clientProperties;
    /**
     * If set to {@code true}, a session will always be created, even if one didn't exist
     * at the start of the request. Defaults to {@code false}.
     */
    private boolean alwaysCreateSession;

    /**
     * @param sessionEnhanceCheckService    用于而外的 session 检验服务, 如客户端特征码等
     * @param sessionRegistry the session registry which should be updated when the
     * @param clientProperties clientProperties
     */
    public EnhanceConcurrentControlAuthenticationStrategy(SessionEnhanceCheckService sessionEnhanceCheckService, SessionRegistry sessionRegistry, ClientProperties clientProperties) {
        super(sessionRegistry);
        this.sessionEnhanceCheckService = sessionEnhanceCheckService;
        this.sessionRegistry = sessionRegistry;
        this.clientProperties = clientProperties;
    }


    /**
     * 1. concurrent control. <br>
     * 2. In addition to the steps from the superclass, the sessionRegistry will be updated
     * with the new session information.
     */
    @Override
    public void onAuthentication(Authentication authentication,
                                 HttpServletRequest request, HttpServletResponse response) {

        // concurrent control
        if (clientProperties.getSession().getSessionNumberControl())
        {
            final List<SessionInformation> sessions = sessionRegistry.getAllSessions(
                    authentication.getPrincipal(), false);

            int sessionCount = sessions.size();
            int allowedSessions = getMaximumSessionsForThisUser(authentication);

            if (sessionCount < allowedSessions) {
                // They haven't got too many login sessions running at present
                return;
            }

            if (allowedSessions == -1) {
                // We permit unlimited logins
                return;
            }

            if (sessionCount == allowedSessions) {
                HttpSession session = request.getSession(false);

                if (session != null) {
                    // Only permit it though if this request is associated with one of the
                    // already registered sessions
                    for (SessionInformation si : sessions) {
                        if (si.getSessionId().equals(session.getId())) {
                            return;
                        }
                    }
                }
                // If the session is null, a new one will be created by the parent class,
                // exceeding the allowed number
            }

            allowableSessionsExceeded(sessions, allowedSessions, sessionRegistry);
        }
        // change session id and session fixation protection
        else
        {
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
                    log.warn("session-fixation attacks: Your servlet container did not change the session ID when a new session was created. You will not be adequately protected against session-fixation attacks");
                }

                onSessionChange(originalSessionId, session, authentication);

                if (this.sessionEnhanceCheckService != null)
                {
                    this.sessionEnhanceCheckService.setEnhanceCheckValue(session, request);
                }
            }

        }

    }

    /**
     * Allows subclasses to customise behaviour when too many sessions are detected.
     *
     * @param sessions either <codes>null</codes> or all unexpired sessions associated with
     * the principal
     * @param allowableSessions the number of concurrent sessions the user is allowed to
     * have
     * @param registry an instance of the <codes>SessionRegistry</codes> for subclass use
     *
     */
    @Override
    protected void allowableSessionsExceeded(List<SessionInformation> sessions,
                                             int allowableSessions, SessionRegistry registry)
            throws SessionAuthenticationException {
        if (exceptionIfMaximumExceeded || (sessions == null)) {
            throw new SessionAuthenticationException(messages.getMessage(
                    "ConcurrentSessionControlAuthenticationStrategy.exceededAllowed",
                    new Object[] {allowableSessions},
                    "Maximum sessions of {0} for this principal exceeded"));
        }

        // Determine least recently used sessions, and mark them for invalidation
        sessions.sort(Comparator.comparing(SessionInformation::getLastRequest));
        int maximumSessionsExceededBy = sessions.size() - allowableSessions + 1;
        List<SessionInformation> sessionsToBeExpired = sessions.subList(0, maximumSessionsExceededBy);
        for (SessionInformation session: sessionsToBeExpired) {
            session.expireNow();
        }
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
     * Sets the <tt>exceptionIfMaximumExceeded</tt> property, which determines whether the
     * user should be prevented from opening more sessions than allowed. If set to
     * <tt>true</tt>, a <tt>SessionAuthenticationException</tt> will be raised which means
     * the user authenticating will be prevented from authenticating. if set to
     * <tt>false</tt>, the user that has already authenticated will be forcibly logged
     * out.
     *
     * @param exceptionIfMaximumExceeded defaults to <tt>false</tt>.
     */
    @Override
    public void setExceptionIfMaximumExceeded(boolean exceptionIfMaximumExceeded) {
        super.setExceptionIfMaximumExceeded(exceptionIfMaximumExceeded);
        this.exceptionIfMaximumExceeded = exceptionIfMaximumExceeded;
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
     * Sets the <tt>maxSessions</tt> property. The default value is 1. Use -1 for
     * unlimited sessions.
     *
     * @param maximumSessions the maximimum number of permitted sessions a user can have
     * open simultaneously.
     */
    @Override
    public void setMaximumSessions(int maximumSessions) {
        Assert.isTrue(
                maximumSessions != 0,
                "MaximumLogins must be either -1 to allow unlimited logins, or a positive integer to specify a maximum");
        super.setMaximumSessions(maximumSessions);
        this.maximumSessions = maximumSessions;
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
