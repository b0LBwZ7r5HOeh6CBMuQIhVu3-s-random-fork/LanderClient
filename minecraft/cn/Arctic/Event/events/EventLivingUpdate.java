package cn.Arctic.Event.events;






import cn.Arctic.Event.Event;
import net.minecraft.entity.Entity;

public class EventLivingUpdate extends Event {
    public Entity entity;

    public EventLivingUpdate(Entity targetEntity) {
        this.entity = targetEntity;
    }

    public Entity getEntity() {
        return entity;
    }



}
