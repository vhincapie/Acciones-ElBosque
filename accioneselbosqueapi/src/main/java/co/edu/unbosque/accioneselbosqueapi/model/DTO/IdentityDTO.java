package co.edu.unbosque.accioneselbosqueapi.model.DTO;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public class IdentityDTO {

    @JsonProperty("given_name")
    private String givenName;

    @JsonProperty("family_name")
    private String familyName;

    @JsonProperty("date_of_birth")
    private String dateOfBirth;

    @JsonProperty("tax_id_type")
    private String taxIdType;

    @JsonProperty("tax_id")
    private String taxId;

    @JsonProperty("country_of_citizenship")
    private String countryOfCitizenship;

    @JsonProperty("country_of_birth")
    private String countryOfBirth;

    @JsonProperty("country_of_tax_residence")
    private String countryOfTaxResidence;

    @JsonProperty("funding_source")
    private List<String> fundingSource;

    @JsonProperty("annual_income_min")
    private String annualIncomeMin;

    @JsonProperty("annual_income_max")
    private String annualIncomeMax;

    @JsonProperty("total_net_worth_min")
    private String totalNetWorthMin;

    @JsonProperty("total_net_worth_max")
    private String totalNetWorthMax;

    @JsonProperty("liquid_net_worth_min")
    private String liquidNetWorthMin;

    @JsonProperty("liquid_net_worth_max")
    private String liquidNetWorthMax;

    @JsonProperty("liquidity_needs")
    private String liquidityNeeds;

    @JsonProperty("investment_experience_with_stocks")
    private String investmentExperienceWithStocks;

    @JsonProperty("investment_experience_with_options")
    private String investmentExperienceWithOptions;

    @JsonProperty("risk_tolerance")
    private String riskTolerance;

    @JsonProperty("investment_objective")
    private String investmentObjective;

    @JsonProperty("investment_time_horizon")
    private String investmentTimeHorizon;

    @JsonProperty("marital_status")
    private String maritalStatus;

    @JsonProperty("number_of_dependents")
    private int numberOfDependents;

    public IdentityDTO() {}

    public IdentityDTO(String givenName, String familyName, String dateOfBirth, String taxIdType, String taxId,
                       String countryOfCitizenship, String countryOfBirth, String countryOfTaxResidence,
                       List<String> fundingSource, String annualIncomeMin, String annualIncomeMax,
                       String totalNetWorthMin, String totalNetWorthMax, String liquidNetWorthMin,
                       String liquidNetWorthMax, String liquidityNeeds, String investmentExperienceWithStocks,
                       String investmentExperienceWithOptions, String riskTolerance, String investmentObjective,
                       String investmentTimeHorizon, String maritalStatus, int numberOfDependents) {
        this.givenName = givenName;
        this.familyName = familyName;
        this.dateOfBirth = dateOfBirth;
        this.taxIdType = taxIdType;
        this.taxId = taxId;
        this.countryOfCitizenship = countryOfCitizenship;
        this.countryOfBirth = countryOfBirth;
        this.countryOfTaxResidence = countryOfTaxResidence;
        this.fundingSource = fundingSource;
        this.annualIncomeMin = annualIncomeMin;
        this.annualIncomeMax = annualIncomeMax;
        this.totalNetWorthMin = totalNetWorthMin;
        this.totalNetWorthMax = totalNetWorthMax;
        this.liquidNetWorthMin = liquidNetWorthMin;
        this.liquidNetWorthMax = liquidNetWorthMax;
        this.liquidityNeeds = liquidityNeeds;
        this.investmentExperienceWithStocks = investmentExperienceWithStocks;
        this.investmentExperienceWithOptions = investmentExperienceWithOptions;
        this.riskTolerance = riskTolerance;
        this.investmentObjective = investmentObjective;
        this.investmentTimeHorizon = investmentTimeHorizon;
        this.maritalStatus = maritalStatus;
        this.numberOfDependents = numberOfDependents;
    }

    public String getGivenName() {
        return givenName;
    }

    public void setGivenName(String givenName) {
        this.givenName = givenName;
    }

    public String getFamilyName() {
        return familyName;
    }

    public void setFamilyName(String familyName) {
        this.familyName = familyName;
    }

    public String getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(String dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public String getTaxIdType() {
        return taxIdType;
    }

    public void setTaxIdType(String taxIdType) {
        this.taxIdType = taxIdType;
    }

    public String getTaxId() {
        return taxId;
    }

    public void setTaxId(String taxId) {
        this.taxId = taxId;
    }

    public String getCountryOfCitizenship() {
        return countryOfCitizenship;
    }

    public void setCountryOfCitizenship(String countryOfCitizenship) {
        this.countryOfCitizenship = countryOfCitizenship;
    }

    public String getCountryOfBirth() {
        return countryOfBirth;
    }

    public void setCountryOfBirth(String countryOfBirth) {
        this.countryOfBirth = countryOfBirth;
    }

    public String getCountryOfTaxResidence() {
        return countryOfTaxResidence;
    }

    public void setCountryOfTaxResidence(String countryOfTaxResidence) {
        this.countryOfTaxResidence = countryOfTaxResidence;
    }

    public List<String> getFundingSource() {
        return fundingSource;
    }

    public void setFundingSource(List<String> fundingSource) {
        this.fundingSource = fundingSource;
    }

    public String getAnnualIncomeMin() {
        return annualIncomeMin;
    }

    public void setAnnualIncomeMin(String annualIncomeMin) {
        this.annualIncomeMin = annualIncomeMin;
    }

    public String getAnnualIncomeMax() {
        return annualIncomeMax;
    }

    public void setAnnualIncomeMax(String annualIncomeMax) {
        this.annualIncomeMax = annualIncomeMax;
    }

    public String getTotalNetWorthMin() {
        return totalNetWorthMin;
    }

    public void setTotalNetWorthMin(String totalNetWorthMin) {
        this.totalNetWorthMin = totalNetWorthMin;
    }

    public String getTotalNetWorthMax() {
        return totalNetWorthMax;
    }

    public void setTotalNetWorthMax(String totalNetWorthMax) {
        this.totalNetWorthMax = totalNetWorthMax;
    }

    public String getLiquidNetWorthMin() {
        return liquidNetWorthMin;
    }

    public void setLiquidNetWorthMin(String liquidNetWorthMin) {
        this.liquidNetWorthMin = liquidNetWorthMin;
    }

    public String getLiquidNetWorthMax() {
        return liquidNetWorthMax;
    }

    public void setLiquidNetWorthMax(String liquidNetWorthMax) {
        this.liquidNetWorthMax = liquidNetWorthMax;
    }

    public String getLiquidityNeeds() {
        return liquidityNeeds;
    }

    public void setLiquidityNeeds(String liquidityNeeds) {
        this.liquidityNeeds = liquidityNeeds;
    }

    public String getInvestmentExperienceWithStocks() {
        return investmentExperienceWithStocks;
    }

    public void setInvestmentExperienceWithStocks(String investmentExperienceWithStocks) {
        this.investmentExperienceWithStocks = investmentExperienceWithStocks;
    }

    public String getInvestmentExperienceWithOptions() {
        return investmentExperienceWithOptions;
    }

    public void setInvestmentExperienceWithOptions(String investmentExperienceWithOptions) {
        this.investmentExperienceWithOptions = investmentExperienceWithOptions;
    }

    public String getRiskTolerance() {
        return riskTolerance;
    }

    public void setRiskTolerance(String riskTolerance) {
        this.riskTolerance = riskTolerance;
    }

    public String getInvestmentObjective() {
        return investmentObjective;
    }

    public void setInvestmentObjective(String investmentObjective) {
        this.investmentObjective = investmentObjective;
    }

    public String getInvestmentTimeHorizon() {
        return investmentTimeHorizon;
    }

    public void setInvestmentTimeHorizon(String investmentTimeHorizon) {
        this.investmentTimeHorizon = investmentTimeHorizon;
    }

    public String getMaritalStatus() {
        return maritalStatus;
    }

    public void setMaritalStatus(String maritalStatus) {
        this.maritalStatus = maritalStatus;
    }

    public int getNumberOfDependents() {
        return numberOfDependents;
    }

    public void setNumberOfDependents(int numberOfDependents) {
        this.numberOfDependents = numberOfDependents;
    }
}
