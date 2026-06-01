package mil.dds.anet.beans;

import com.fasterxml.jackson.annotation.JsonIgnore;
import graphql.GraphQLContext;
import io.leangen.graphql.annotations.GraphQLInputField;
import io.leangen.graphql.annotations.GraphQLQuery;
import io.leangen.graphql.annotations.GraphQLRootContext;
import java.util.Comparator;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import mil.dds.anet.utils.IdDataLoaderKey;
import mil.dds.anet.views.UuidFetcher;

public class ReportPerson extends Person {

  public static final Comparator<ReportPerson> COMPARATOR = Comparator
      .comparing(ReportPerson::isAttendee, Comparator.reverseOrder())
      .thenComparing(ReportPerson::isInterlocutor)
      .thenComparing(ReportPerson::isPrimary, Comparator.reverseOrder())
      .thenComparing(ReportPerson::isAuthor, Comparator.reverseOrder())
      .thenComparing(ReportPerson::getFamilyName).thenComparing(ReportPerson::getGivenName)
      .thenComparing(ReportPerson::getUuid).thenComparing(ReportPerson::getPositionInReportUuid);;

  @GraphQLQuery
  @GraphQLInputField
  boolean primary;
  @GraphQLQuery
  @GraphQLInputField
  boolean author;
  @GraphQLQuery
  @GraphQLInputField
  boolean attendee;
  @GraphQLQuery
  @GraphQLInputField
  boolean interlocutor;

  // Lazy Loaded
  // annotated below
  private ForeignObjectHolder<Position> positionInReport = new ForeignObjectHolder<>();

  public ReportPerson() {
    this.primary = false; // Default
    this.author = false;
    this.attendee = true;
    this.interlocutor = true;
  }

  public boolean isPrimary() {
    return primary;
  }

  public void setPrimary(boolean primary) {
    this.primary = primary;
  }

  public boolean isAuthor() {
    return author;
  }

  public void setAuthor(boolean author) {
    this.author = author;
  }

  public boolean isAttendee() {
    return attendee;
  }

  public void setAttendee(boolean attendee) {
    this.attendee = attendee;
  }

  public boolean isInterlocutor() {
    return interlocutor;
  }

  public void setInterlocutor(boolean interlocutor) {
    this.interlocutor = interlocutor;
  }

  @GraphQLQuery(name = "positionInReport")
  public CompletableFuture<Position> loadPositionInReport(
      @GraphQLRootContext GraphQLContext context) {
    if (positionInReport.hasForeignObject()) {
      return CompletableFuture.completedFuture(positionInReport.getForeignObject());
    }
    return new UuidFetcher<Position>()
        .load(context, IdDataLoaderKey.POSITIONS, positionInReport.getForeignUuid())
        .thenApply(o -> {
          positionInReport.setForeignObject(o);
          return o;
        });
  }

  @JsonIgnore
  public void setPositionInReportUuid(String positionInReportUuid) {
    this.positionInReport = new ForeignObjectHolder<>(positionInReportUuid);
  }

  @JsonIgnore
  public String getPositionInReportUuid() {
    return positionInReport.getForeignUuid();
  }

  @GraphQLInputField(name = "positionInReport")
  public void setPositionInReport(Position position) {
    this.positionInReport = new ForeignObjectHolder<>(position);
  }

  public Position getPositionInReport() {
    return positionInReport.getForeignObject();
  }

  @Override
  public boolean equals(Object o) {
    if (!(o instanceof ReportPerson)) {
      return false;
    }
    ReportPerson rp = (ReportPerson) o;
    return super.equals(o) && Objects.equals(rp.isPrimary(), primary)
        && Objects.equals(rp.isAuthor(), author) && Objects.equals(rp.isAttendee(), attendee)
        && Objects.equals(rp.isInterlocutor(), interlocutor)
        && Objects.equals(rp.getPositionInReportUuid(), getPositionInReportUuid());
  }

  @Override
  public int hashCode() {
    return Objects.hash(super.hashCode(), primary, author, attendee, interlocutor);
  }
}
