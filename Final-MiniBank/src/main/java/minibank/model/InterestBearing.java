package minibank.model;
public interface InterestBearing {
    double interestRate();
    default double yearlyInterest() {
        return balanceForInterest() * interestRate() / 100.0;
    }
    long balanceForInterest();
}
