package model;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: User
 * Date: 30.05.13
 * Time: 15:08
 */
public class SearchableModel {

    private Integer id;
    private List<String> urls;

    public SearchableModel(Integer id, List<String> urls) {
        this.id = id;
        this.urls = urls;
    }

    public Integer getId() {
        return id;
    }

    public List<String> getUrls() {
        return urls;
    }

    @Override
    public int hashCode() {
        int result = (id != null ? id.hashCode() : 0);
        result = 31 * result + (urls != null ? urls.hashCode() : 0);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof SearchableModel)) {
            return false;
        }

        SearchableModel that = (SearchableModel) obj;

        if (id != null ? !(id.equals(that.id)) : that.id != null) {
            return false;
        }

        //noinspection RedundantIfStatement
        if (urls != null ? !(urls.equals(that.urls)) : that.urls != null) {
            return false;
        }

        return true;
    }
}
