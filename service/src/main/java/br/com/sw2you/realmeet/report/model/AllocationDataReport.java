package br.com.sw2you.realmeet.report.model;

import java.time.LocalDate;
import java.util.Objects;

public class AllocationDataReport extends AbstractReportData {
    private final LocalDate dateFrom;
    private final LocalDate dateTo;

    public AllocationDataReport(Builder builder) {
        super(builder.email);
        dateFrom = builder.dateFrom;
        dateTo = builder.dateTo;
    }

    public LocalDate getDateFrom() {
        return dateFrom;
    }

    public LocalDate getDateTo() {
        return dateTo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AllocationDataReport that = (AllocationDataReport) o;
        return Objects.equals(dateFrom, that.dateFrom) && Objects.equals(dateTo, that.dateTo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), dateFrom, dateTo);
    }

    @Override
    public String toString() {
        return "AllocationDataReport{" + "dateFrom=" + dateFrom + ", dateTo=" + dateTo + '}';
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static final class Builder {
        private String email;
        private LocalDate dateFrom;
        private LocalDate dateTo;

        private Builder() {}

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder dateFrom(LocalDate dateFrom) {
            this.dateFrom = dateFrom;
            return this;
        }

        public Builder dateTo(LocalDate dateTo) {
            this.dateTo = dateTo;
            return this;
        }

        public AllocationDataReport build() {
            return new AllocationDataReport(this);
        }
    }
}
