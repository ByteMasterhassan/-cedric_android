package com.cedricapp.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class StripeCustomerModel {
    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("object")
    @Expose
    private String object;
    @SerializedName("address")
    @Expose
    private Object address;
    @SerializedName("balance")
    @Expose
    private Integer balance;
    @SerializedName("created")
    @Expose
    private Integer created;
    @SerializedName("currency")
    @Expose
    private String currency;
    @SerializedName("default_source")
    @Expose
    private Object defaultSource;
    @SerializedName("delinquent")
    @Expose
    private Boolean delinquent;
    @SerializedName("description")
    @Expose
    private String description;
    @SerializedName("discount")
    @Expose
    private Object discount;
    @SerializedName("email")
    @Expose
    private Object email;
    @SerializedName("invoice_prefix")
    @Expose
    private String invoicePrefix;
    @SerializedName("invoice_settings")
    @Expose
    private InvoiceSettings invoiceSettings;
    @SerializedName("livemode")
    @Expose
    private Boolean livemode;
    @SerializedName("metadata")
    @Expose
    private Metadata metadata;
    @SerializedName("name")
    @Expose
    private Object name;
    @SerializedName("next_invoice_sequence")
    @Expose
    private Integer nextInvoiceSequence;
    @SerializedName("phone")
    @Expose
    private Object phone;
    @SerializedName("preferred_locales")
    @Expose
    private List<Object> preferredLocales = null;
    @SerializedName("shipping")
    @Expose
    private Object shipping;
    @SerializedName("tax_exempt")
    @Expose
    private String taxExempt;
    @SerializedName("test_clock")
    @Expose
    private Object testClock;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public Object getAddress() {
        return address;
    }

    public void setAddress(Object address) {
        this.address = address;
    }

    public Integer getBalance() {
        return balance;
    }

    public void setBalance(Integer balance) {
        this.balance = balance;
    }

    public Integer getCreated() {
        return created;
    }

    public void setCreated(Integer created) {
        this.created = created;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public Object getDefaultSource() {
        return defaultSource;
    }

    public void setDefaultSource(Object defaultSource) {
        this.defaultSource = defaultSource;
    }

    public Boolean getDelinquent() {
        return delinquent;
    }

    public void setDelinquent(Boolean delinquent) {
        this.delinquent = delinquent;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Object getDiscount() {
        return discount;
    }

    public void setDiscount(Object discount) {
        this.discount = discount;
    }

    public Object getEmail() {
        return email;
    }

    public void setEmail(Object email) {
        this.email = email;
    }

    public String getInvoicePrefix() {
        return invoicePrefix;
    }

    public void setInvoicePrefix(String invoicePrefix) {
        this.invoicePrefix = invoicePrefix;
    }

    public InvoiceSettings getInvoiceSettings() {
        return invoiceSettings;
    }

    public void setInvoiceSettings(InvoiceSettings invoiceSettings) {
        this.invoiceSettings = invoiceSettings;
    }

    public Boolean getLivemode() {
        return livemode;
    }

    public void setLivemode(Boolean livemode) {
        this.livemode = livemode;
    }

    public Metadata getMetadata() {
        return metadata;
    }

    public void setMetadata(Metadata metadata) {
        this.metadata = metadata;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public Integer getNextInvoiceSequence() {
        return nextInvoiceSequence;
    }

    public void setNextInvoiceSequence(Integer nextInvoiceSequence) {
        this.nextInvoiceSequence = nextInvoiceSequence;
    }

    public Object getPhone() {
        return phone;
    }

    public void setPhone(Object phone) {
        this.phone = phone;
    }

    public List<Object> getPreferredLocales() {
        return preferredLocales;
    }

    public void setPreferredLocales(List<Object> preferredLocales) {
        this.preferredLocales = preferredLocales;
    }

    public Object getShipping() {
        return shipping;
    }

    public void setShipping(Object shipping) {
        this.shipping = shipping;
    }

    public String getTaxExempt() {
        return taxExempt;
    }

    public void setTaxExempt(String taxExempt) {
        this.taxExempt = taxExempt;
    }

    public Object getTestClock() {
        return testClock;
    }

    public void setTestClock(Object testClock) {
        this.testClock = testClock;
    }

    public class InvoiceSettings {

        @SerializedName("custom_fields")
        @Expose
        private Object customFields;
        @SerializedName("default_payment_method")
        @Expose
        private Object defaultPaymentMethod;
        @SerializedName("footer")
        @Expose
        private Object footer;
        @SerializedName("rendering_options")
        @Expose
        private Object renderingOptions;

        public Object getCustomFields() {
            return customFields;
        }

        public void setCustomFields(Object customFields) {
            this.customFields = customFields;
        }

        public Object getDefaultPaymentMethod() {
            return defaultPaymentMethod;
        }

        public void setDefaultPaymentMethod(Object defaultPaymentMethod) {
            this.defaultPaymentMethod = defaultPaymentMethod;
        }

        public Object getFooter() {
            return footer;
        }

        public void setFooter(Object footer) {
            this.footer = footer;
        }

        public Object getRenderingOptions() {
            return renderingOptions;
        }

        public void setRenderingOptions(Object renderingOptions) {
            this.renderingOptions = renderingOptions;
        }

    }

    public class Metadata {


    }

}
