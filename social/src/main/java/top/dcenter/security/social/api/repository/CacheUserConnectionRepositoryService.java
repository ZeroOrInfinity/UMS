package top.dcenter.security.social.api.repository;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionKey;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.DuplicateConnectionException;
import org.springframework.social.connect.NoSuchConnectionException;
import org.springframework.social.connect.NotConnectedException;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.util.MultiValueMap;

import java.util.List;
import java.util.Set;

/**
 * 对 {@link UsersConnectionRepository} 与 {@link org.springframework.social.connect.ConnectionRepository} 的 缓存服务
 * @author zyw23
 * @version V1.0
 * Created by 2020/6/11 18:32
 */
public interface CacheUserConnectionRepositoryService {
    /**
     * 在执行 {@link UsersConnectionRepository#findUserIdsWithConnection(Connection)} 方法前, 查询缓存
     * @param connection the service provider connection resulting from the provider sign-in attempt
     * @return the user ids associated with the connection.
     */
    List<String> findUserIdsWithConnection(Connection<?> connection);

    /**
     * 在执行 {@link UsersConnectionRepository#findUserIdsConnectedTo(String, Set)} 方法前, 查询缓存
     * @param providerId the provider id, e.g. "facebook"
     * @param providerUserIds the set of provider user ids e.g. ("125600", "131345", "54321").
     * @return the set of user ids connected to those service provider users, or empty if none.
     */
    Set<String> findUserIdsConnectedTo(String providerId, Set<String> providerUserIds);

    /**
     * 在执行 {@link ConnectionRepository#findAllConnections()} 方法前, 查询缓存
     * @return all connections the current user has across all providers.
     */
    MultiValueMap<String, Connection<?>> findAllConnections();

    /**
     * 在执行 {@link ConnectionRepository#findConnections(String)} 方法前, 查询缓存
     * @param providerId the provider id e.g. "facebook"
     * @return the connections the user has to the provider, or an empty list if none
     */
    List<Connection<?>> findConnections(String providerId);

    /**
     * 在执行 {@link ConnectionRepository#findConnections(Class)} 方法前, 查询缓存
     * @param <A> the API parameterized type
     * @param apiType the API type e.g. Facebook.class or Twitter.class
     * @return the connections the user has to the provider of the API, or an empty list if none
     */
    <A> List<Connection<A>> findConnections(Class<A> apiType);

    /**
     * 在执行 {@link ConnectionRepository#findConnectionsToUsers(MultiValueMap)} 方法前, 查询缓存
     * @param providerUserIds the provider users map
     * @return the provider user connection map
     */
    MultiValueMap<String, Connection<?>> findConnectionsToUsers(MultiValueMap<String, String> providerUserIds);

    /**
     * 在执行 {@link ConnectionRepository#getConnection(ConnectionKey)} 方法前, 查询缓存
     * @param connectionKey the service provider connection key
     * @return the connection
     * @throws NoSuchConnectionException if no such connection exists for the current user
     */
    Connection<?> getConnection(ConnectionKey connectionKey);

    /**
     * 在执行 {@link ConnectionRepository#getConnection(Class, String)} 方法前, 查询缓存
     * @param <A> the API parameterized type
     * @param apiType the API type e.g. Facebook.class or Twitter.class
     * @param providerUserId the provider user e.g. "126500".
     * @return the connection
     * @throws NoSuchConnectionException if no such connection exists for the current user
     */
    <A> Connection<A> getConnection(Class<A> apiType, String providerUserId);

    /**
     * 在执行 {@link ConnectionRepository#getPrimaryConnection(Class)} 方法前, 查询缓存
     * @param <A> the API parameterized type
     * @param apiType the API type e.g. Facebook.class or Twitter.class
     * @return the primary connection
     * @throws NotConnectedException if the user is not connected to the provider of the API
     */
    <A> Connection<A> getPrimaryConnection(Class<A> apiType);

    /**
     * 在执行 {@link ConnectionRepository#findPrimaryConnection(Class)} 方法前, 查询缓存
     * @param <A> the API parameterized type
     * @param apiType the API type e.g. Facebook.class or Twitter.class
     * @return the primary connection, or <code>null</code> if not found
     */
    <A> Connection<A> findPrimaryConnection(Class<A> apiType);

    /**
     * 在执行 {@link ConnectionRepository#addConnection(Connection)} 时, 添加缓存
     * @param connection the new connection to add to this repository
     * @throws DuplicateConnectionException if the user already has this connection
     */
    void addConnection(Connection<?> connection);

    /**
     * 在执行 {@link ConnectionRepository#updateConnection(Connection)} 时, 更新缓存
     * @param connection the existing connection to update in this repository
     */
    void updateConnection(Connection<?> connection);

    /**
     * 在执行 {@link ConnectionRepository#removeConnections(String)} 时, 清楚缓存
     * @param providerId the provider id e.g. 'facebook'
     */
    void removeConnections(String providerId);

    /**
     * 在执行 {@link ConnectionRepository#removeConnection(ConnectionKey)} 时, 清楚缓存
     * @param connectionKey the connection key
     */
    void removeConnection(ConnectionKey connectionKey);
}
