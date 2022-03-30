package pojo;

public class Entity {
    byte[] feature;
    byte[] message;

    public Entity(
            byte[] feature,
            byte[] message
    ) {
        this.feature = feature;
        this.message = message;
    }

    public String toString() {
        return "Entity {feature :" + new String(feature)+" message: "+ new String(message)+"}";
    }
}
