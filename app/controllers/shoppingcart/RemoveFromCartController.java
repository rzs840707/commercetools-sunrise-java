package controllers.shoppingcart;

import com.commercetools.sunrise.core.components.controllers.RegisteredComponents;
import com.commercetools.sunrise.core.controllers.cache.NoCache;
import com.commercetools.sunrise.core.controllers.metrics.LogMetrics;
import com.commercetools.sunrise.core.renderers.ContentRenderer;
import com.commercetools.sunrise.core.renderers.TemplateControllerComponentsSupplier;
import com.commercetools.sunrise.core.reverserouters.shoppingcart.cart.CartReverseRouter;
import com.commercetools.sunrise.models.carts.CartDiscountCodesExpansionControllerComponent;
import com.commercetools.sunrise.models.carts.CartOperationsControllerComponentSupplier;
import com.commercetools.sunrise.shoppingcart.CartFinder;
import com.commercetools.sunrise.shoppingcart.content.viewmodels.CartPageContentFactory;
import com.commercetools.sunrise.shoppingcart.remove.RemoveFromCartControllerAction;
import com.commercetools.sunrise.shoppingcart.remove.RemoveFromCartFormData;
import com.commercetools.sunrise.shoppingcart.remove.SunriseRemoveFromCartController;
import com.commercetools.sunrise.wishlist.MiniWishlistControllerComponent;
import io.sphere.sdk.carts.Cart;
import play.data.FormFactory;
import play.mvc.Result;

import javax.inject.Inject;
import java.util.concurrent.CompletionStage;

@LogMetrics
@NoCache
@RegisteredComponents({
        TemplateControllerComponentsSupplier.class,
        CartOperationsControllerComponentSupplier.class,
        CartDiscountCodesExpansionControllerComponent.class,
        MiniWishlistControllerComponent.class
})
public final class RemoveFromCartController extends SunriseRemoveFromCartController {

    private final CartReverseRouter cartReverseRouter;

    @Inject
    public RemoveFromCartController(final ContentRenderer contentRenderer,
                                    final FormFactory formFactory,
                                    final RemoveFromCartFormData formData,
                                    final CartFinder cartFinder,
                                    final RemoveFromCartControllerAction removeFromCartControllerAction,
                                    final CartPageContentFactory cartPageContentFactory,
                                    final CartReverseRouter cartReverseRouter) {
        super(contentRenderer, formFactory, formData, cartFinder, removeFromCartControllerAction, cartPageContentFactory);
        this.cartReverseRouter = cartReverseRouter;
    }

    @Override
    public String getTemplateName() {
        return "cart";
    }

    @Override
    public String getCmsPageKey() {
        return "default";
    }

    @Override
    public CompletionStage<Result> handleNotFoundCart() {
        return redirectToCall(cartReverseRouter.cartDetailPageCall());
    }

    @Override
    public CompletionStage<Result> handleSuccessfulAction(final Cart updatedCart, final RemoveFromCartFormData formData) {
        return redirectToCall(cartReverseRouter.cartDetailPageCall());
    }
}
