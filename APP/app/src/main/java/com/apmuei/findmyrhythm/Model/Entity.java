package com.apmuei.findmyrhythm.Model;

public class Entity {

    // Primary key of every entities
    private  String id;

    public Entity(){}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean hasId() {
        return id != null && !id.equals("");
    }

    @Override
    public boolean equals(Object o) {
        if(this == o)
            return true;

        if (!(o instanceof Entity))
            return false;

        Entity entity = (Entity) o;
        return id.equals(entity.id);
    }
}
