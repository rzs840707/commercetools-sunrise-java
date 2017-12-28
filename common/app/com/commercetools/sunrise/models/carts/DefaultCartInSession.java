package com.commercetools.sunrise.models.carts;

import com.commercetools.sunrise.core.sessions.CacheableObjectStoringSessionStrategy;
import com.commercetools.sunrise.core.sessions.DataFromResourceStoringOperations;
import com.commercetools.sunrise.core.sessions.ObjectStoringSessionStrategy;
import io.sphere.sdk.carts.Cart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import play.Configuration;

import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Optional;

/**
 * Keeps some parts from the cart in session, such as cart ID and mini cart.
 */
@Singleton
public class DefaultCartInSession extends DataFromResourceStoringOperations<Cart> implements CartInSession {

    private static final Logger LOGGER = LoggerFactory.getLogger(CartInSession.class);
    private static final String DEFAULT_CART_ID_SESSION_KEY = "sunrise-cart-id";
    private static final String DEFAULT_MINI_CART_SESSION_KEY = "sunrise-mini-cart";

    private final String cartIdSessionKey;
    private final String cartSessionKey;
    private final ObjectStoringSessionStrategy session;

    @Inject
    public DefaultCartInSession(final Configuration configuration, final CacheableObjectStoringSessionStrategy session) {
        this.cartIdSessionKey = configuration.getString("session.cart.cartId", DEFAULT_CART_ID_SESSION_KEY);
        this.cartSessionKey = configuration.getString("session.cart.miniCart", DEFAULT_MINI_CART_SESSION_KEY);
        this.session = session;
    }

    @Override
    protected final Logger getLogger() {
        return LOGGER;
    }

    protected final String getCartIdSessionKey() {
        return cartIdSessionKey;
    }

    protected final String getCartSessionKey() {
        return cartSessionKey;
    }

    protected final ObjectStoringSessionStrategy getSession() {
        return session;
    }

    @Override
    public Optional<String> findCartId() {
        return session.findValueByKey(cartIdSessionKey);
    }

    @Override
    public Optional<Cart> findCart() {
        return session.findObjectByKey(cartSessionKey, Cart.class);
    }

    @Override
    public void store(@Nullable final Cart cart) {
        super.store(cart);
    }

    @Override
    public void remove() {
        super.remove();
    }

    @Override
    protected void storeAssociatedData(final Cart cart) {
        session.overwriteObjectByKey(cartSessionKey, cart);
        session.overwriteValueByKey(cartIdSessionKey, cart.getId());
    }

    @Override
    protected void removeAssociatedData() {
        session.removeObjectByKey(cartSessionKey);
        session.removeValueByKey(cartIdSessionKey);
    }
}
