package model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: web
 * Date: 3/30/2014
 * Time: 10:35 AM
 */
public class SearchResultModel {

    private List<SearchableModel> indexedAdverts;
    private List<SearchableModel> unindexedAdverts;

    public SearchResultModel(
            List<SearchableModel> indexedAdverts, List<SearchableModel> unindexedAdverts) {
        this.indexedAdverts = indexedAdverts;
        this.unindexedAdverts = unindexedAdverts;
    }

    @Override
    public int hashCode() {
        int result = indexedAdverts != null ? indexedAdverts.hashCode() : 0;
        result = 31 * result + (unindexedAdverts != null ? unindexedAdverts.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SearchResultModel)) {
            return false;
        }

        SearchResultModel that = (SearchResultModel) obj;

        if (indexedAdverts != null ? !indexedAdverts.equals(that.indexedAdverts)
                : that.indexedAdverts != null) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (unindexedAdverts != null ? !unindexedAdverts.equals(that.unindexedAdverts)
                : that.unindexedAdverts != null) {
            return false;
        }

        return true;
    }
}
