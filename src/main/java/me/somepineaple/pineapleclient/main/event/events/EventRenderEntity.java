package me.somepineaple.pineapleclient.main.event.events;

import me.somepineaple.pineapleclient.main.event.EventCancellable;
import net.minecraft.entity.Entity;

public class EventRenderEntity extends EventCancellable {
    public final Entity entity;
    public final Type type;

    public EventRenderEntity(Entity e, Type t) {
        entity = e;
        type = t;
    }

    public enum Type {
        TEXTURE, COLOR
    }

    public Entity getEntity() {
        return entity;
    }

    public Type getType() {
        return type;
    }

    public static class Head extends EventRenderEntity {
        public Head(Entity e, Type t) {
            super(e, t);
        }
    }

    public static class Return extends EventRenderEntity {
        public Return(Entity e, Type t) {
            super(e, t);
        }
    }
}
