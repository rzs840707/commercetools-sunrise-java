package com.commercetools.sunrise.framework.checkout.payment.viewmodels;

import com.commercetools.sunrise.framework.injection.RequestScoped;
import com.commercetools.sunrise.common.forms.FormFieldWithOptions;
import com.commercetools.sunrise.common.models.FormFieldViewModelFactory;
import io.sphere.sdk.payments.PaymentMethodInfo;
import play.data.Form;

import javax.inject.Inject;
import java.util.Collections;
import java.util.List;

import static java.util.stream.Collectors.toList;

@RequestScoped
public class PaymentMethodFormFieldViewModelFactory extends FormFieldViewModelFactory<PaymentMethodFormFieldViewModel, PaymentMethodInfo> {

    private final PaymentFormSelectableOptionViewModelFactory paymentFormSelectableOptionViewModelFactory;

    @Inject
    public PaymentMethodFormFieldViewModelFactory(final PaymentFormSelectableOptionViewModelFactory paymentFormSelectableOptionViewModelFactory) {
        this.paymentFormSelectableOptionViewModelFactory = paymentFormSelectableOptionViewModelFactory;
    }

    @Override
    protected List<PaymentMethodInfo> defaultOptions() {
        return Collections.emptyList();
    }

    @Override
    protected PaymentMethodFormFieldViewModel getViewModelInstance() {
        return new PaymentMethodFormFieldViewModel();
    }

    @Override
    public final PaymentMethodFormFieldViewModel create(final FormFieldWithOptions<PaymentMethodInfo> data) {
        return super.create(data);
    }

    @Override
    public final PaymentMethodFormFieldViewModel createWithDefaultOptions(final Form.Field formField) {
        return super.createWithDefaultOptions(formField);
    }

    @Override
    protected final void initialize(final PaymentMethodFormFieldViewModel model, final FormFieldWithOptions<PaymentMethodInfo> data) {
        fillList(model, data);
    }

    protected void fillList(final PaymentMethodFormFieldViewModel model, final FormFieldWithOptions<PaymentMethodInfo> data) {
        model.setList(data.getFormOptions().stream()
                .map(paymentMethodInfo -> paymentFormSelectableOptionViewModelFactory.create(paymentMethodInfo, data.getFormField().value()))
                .collect(toList()));
    }
}
