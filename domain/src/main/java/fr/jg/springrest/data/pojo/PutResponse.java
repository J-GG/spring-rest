package fr.jg.springrest.data.pojo;

public class PutResponse<T> {

    private boolean created;

    private T resource;

    public PutResponse(final boolean created) {
        this.created = created;
    }

    public PutResponse(final boolean created, final T resource) {
        this.created = created;
        this.resource = resource;
    }

    public boolean isCreated() {
        return this.created;
    }

    public void setCreated(final boolean created) {
        this.created = created;
    }

    public T getResource() {
        return this.resource;
    }

    public void setResource(final T resource) {
        this.resource = resource;
    }
}
