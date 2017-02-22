package com.commercetools.sunrise.productcatalog.productoverview.viewmodels;

import com.commercetools.sunrise.framework.injection.RequestScoped;
import com.commercetools.sunrise.common.models.ViewModelFactory;
import com.commercetools.sunrise.productcatalog.productdetail.ProductWithVariant;
import io.sphere.sdk.products.ProductProjection;
import io.sphere.sdk.products.ProductVariant;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

@RequestScoped
public class ProductListViewModelFactory extends ViewModelFactory<ProductListViewModel, Iterable<ProductProjection>> {

    private final ProductThumbnailViewModelFactory productThumbnailViewModelFactory;

    @Inject
    public ProductListViewModelFactory(final ProductThumbnailViewModelFactory productThumbnailViewModelFactory) {
        this.productThumbnailViewModelFactory = productThumbnailViewModelFactory;
    }

    @Override
    protected ProductListViewModel getViewModelInstance() {
        return new ProductListViewModel();
    }

    @Override
    public final ProductListViewModel create(final Iterable<ProductProjection> data) {
        return super.create(data);
    }

    @Override
    protected final void initialize(final ProductListViewModel model, final Iterable<ProductProjection> data) {
        fillList(model, data);
    }

    protected void fillList(final ProductListViewModel model, final Iterable<ProductProjection> products) {
        final List<ProductThumbnailViewModel> list = new ArrayList<>();
        products.forEach(product -> list.add(productThumbnailViewModelFactory.create(createProductWithVariant(product))));
        model.setList(list);
    }

    private ProductWithVariant createProductWithVariant(final ProductProjection product) {
        final ProductVariant selectedVariant = product.findFirstMatchingVariant()
                .orElseGet(product::getMasterVariant);
        return ProductWithVariant.of(product, selectedVariant);
    }
}
