package com.commercetools.sunrise.myaccount.mydetails;

import com.commercetools.sunrise.common.controllers.WithFormFlow;
import com.commercetools.sunrise.common.controllers.WithTemplateName;
import com.commercetools.sunrise.framework.annotations.IntroducingMultiControllerComponents;
import com.commercetools.sunrise.framework.annotations.SunriseRoute;
import com.commercetools.sunrise.hooks.events.CustomerUpdatedHook;
import com.commercetools.sunrise.myaccount.CustomerFinder;
import com.commercetools.sunrise.myaccount.common.SunriseFrameworkMyAccountController;
import io.sphere.sdk.client.ClientErrorException;
import io.sphere.sdk.commands.UpdateAction;
import io.sphere.sdk.customers.Customer;
import io.sphere.sdk.customers.CustomerName;
import io.sphere.sdk.customers.commands.CustomerUpdateCommand;
import io.sphere.sdk.customers.commands.updateactions.ChangeEmail;
import io.sphere.sdk.customers.commands.updateactions.SetFirstName;
import io.sphere.sdk.customers.commands.updateactions.SetLastName;
import io.sphere.sdk.customers.commands.updateactions.SetTitle;
import play.data.Form;
import play.libs.concurrent.HttpExecution;
import play.mvc.Result;
import play.twirl.api.Html;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletionStage;

import static java.util.Collections.singletonList;
import static java.util.concurrent.CompletableFuture.completedFuture;

@IntroducingMultiControllerComponents(MyPersonalDetailsThemeLinksControllerComponent.class)
public abstract class SunriseMyPersonalDetailsController extends SunriseFrameworkMyAccountController implements WithTemplateName, WithFormFlow<MyPersonalDetailsFormData, Customer, Customer> {

    private final MyPersonalDetailsPageContentFactory myPersonalDetailsPageContentFactory;
    private final CustomerFinder customerFinder;

    protected SunriseMyPersonalDetailsController(final MyPersonalDetailsPageContentFactory myPersonalDetailsPageContentFactory, final CustomerFinder customerFinder) {
        this.myPersonalDetailsPageContentFactory = myPersonalDetailsPageContentFactory;
        this.customerFinder = customerFinder;
    }

    @Override
    public Set<String> getFrameworkTags() {
        final Set<String> frameworkTags = super.getFrameworkTags();
        frameworkTags.addAll(singletonList("my-personal-details"));
        return frameworkTags;
    }

    @Override
    public String getTemplateName() {
        return "my-account-personal-details";
    }

    @Override
    public Class<? extends MyPersonalDetailsFormData> getFormDataClass() {
        return DefaultMyPersonalDetailsFormData.class;
    }

    @SunriseRoute("myPersonalDetailsPageCall")
    public CompletionStage<Result> show(final String languageTag) {
        return doRequest(() -> customerFinder.findCustomer()
                .thenComposeAsync(customer -> customer
                            .map(this::showForm)
                            .orElseGet(this::handleNotFoundCustomer),
                        HttpExecution.defaultContext()));
    }

    @SunriseRoute("myPersonalDetailsProcessFormCall")
    public CompletionStage<Result> process(final String languageTag) {
        return doRequest(() -> customerFinder.findCustomer()
                .thenComposeAsync(customer -> customer
                                .map(this::validateForm)
                                .orElseGet(this::handleNotFoundCustomer),
                        HttpExecution.defaultContext()));
    }

    @Override
    public CompletionStage<? extends Customer> doAction(final MyPersonalDetailsFormData formData, final Customer customer) {
        return updateCustomer(formData, customer);
    }

    @Override
    public CompletionStage<Result> handleClientErrorFailedAction(final Form<? extends MyPersonalDetailsFormData> form, final Customer customer, final ClientErrorException clientErrorException) {
        saveUnexpectedFormError(form, clientErrorException);
        return asyncBadRequest(renderPage(form, customer, null));
    }

    @Override
    public abstract CompletionStage<Result> handleSuccessfulAction(final MyPersonalDetailsFormData formData, final Customer customer, final Customer updatedCustomer);

    @Override
    public CompletionStage<Html> renderPage(final Form<? extends MyPersonalDetailsFormData> form, final Customer customer, @Nullable final Customer updatedCustomer) {
        final MyPersonalDetailsControllerData myPersonalDetailsControllerData = new MyPersonalDetailsControllerData(form, customer, updatedCustomer);
        final MyPersonalDetailsPageContent pageContent = myPersonalDetailsPageContentFactory.create(myPersonalDetailsControllerData);
        return renderPageWithTemplate(pageContent, getTemplateName());
    }

    @Override
    public void fillFormData(final MyPersonalDetailsFormData formData, final Customer customer) {
        formData.applyCustomerName(customer.getName());
        formData.setEmail(customer.getEmail());
    }

    protected CompletionStage<Customer> updateCustomer(final MyPersonalDetailsFormData formData, final Customer customer) {
        final List<UpdateAction<Customer>> updateActions = buildUpdateActions(formData, customer);
        if (!updateActions.isEmpty()) {
            return sphere().execute(CustomerUpdateCommand.of(customer, updateActions))
                    .thenApplyAsync(updatedCustomer -> {
                        CustomerUpdatedHook.runHook(hooks(), updatedCustomer);
                        return updatedCustomer;
                    }, HttpExecution.defaultContext());
        } else {
            return completedFuture(customer);
        }
    }

    protected List<UpdateAction<Customer>> buildUpdateActions(final MyPersonalDetailsFormData formData, final Customer customer) {
        final List<UpdateAction<Customer>> updateActions = new ArrayList<>();
        final CustomerName customerName = formData.toCustomerName();
        if (!Objects.equals(customer.getTitle(), customerName.getTitle())) {
            updateActions.add(SetTitle.of(customerName.getTitle()));
        }
        if (!Objects.equals(customer.getFirstName(), customerName.getFirstName())) {
            updateActions.add(SetFirstName.of(customerName.getFirstName()));
        }
        if (!Objects.equals(customer.getLastName(), customerName.getLastName())) {
            updateActions.add(SetLastName.of(customerName.getLastName()));
        }
        if (!Objects.equals(customer.getEmail(), formData.getEmail())) {
            updateActions.add(ChangeEmail.of(formData.getEmail()));
        }
        return updateActions;
    }
}
