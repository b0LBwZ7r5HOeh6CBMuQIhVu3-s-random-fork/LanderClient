/*
 * Decompiled with CFR 0_132.
 */
package cn.Arctic.Event;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

import org.newdawn.slick.util.ClasspathLocation;

import cn.Arctic.Event.Listener.EventHandler;
import cn.Arctic.Event.Listener.Listener;
import cn.Arctic.Event.events.EventTick;
import cn.Arctic.Event.events.Update.EventPostUpdate;
import cn.Arctic.Event.events.Update.EventPreUpdate;
import cn.Arctic.Module.Module;
import cn.Arctic.Util.Chat.Helper;

public class EventBus {
    private static ConcurrentHashMap<Class<? extends Event>, List<Handler>> registry = new ConcurrentHashMap();
    private final Comparator<Handler> comparator = (h, h1) -> Byte.compare(h.priority, h1.priority);
    private final MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static final EventBus instance = new EventBus();

    public static EventBus getInstance() {
        return instance;
    }
    
//    public void subscribe(final Object subscriber) {
//        final Method[] methods = subscriber.getClass().getDeclaredMethods();
//        final Map<Class<?>, List<CallLocation>> eventClassMethodMapRef = this.eventClassMethodMap;
//        for (int i = methods.length - 1; i >= 0; --i) {
//            final Method method = methods[i];
//            final Listener listener = method.getAnnotation(Listener.class);
//            if (listener != null) {
//                final Class[] params;
//                final int paramsLength;
//                if ((paramsLength = (params = method.getParameterTypes()).length) <= 1) {
//                    final Class<?> eventClass = listener.value();
//                    if (paramsLength != 1 || eventClass == params[0]) {
//                        final ClasspathLocation callLoc = new ClasspathLocation();
//                        if (eventClassMethodMapRef.containsKey(eventClass)) {
//                            eventClassMethodMapRef.get(eventClass).add(callLoc);
//                        }
//                        else {
//                            eventClassMethodMapRef.put(eventClass, Arrays.asList(callLoc));
//                        }
//                    }
//                }
//            }
//        }
//    }

    public /* varargs */ void register(Object ... objs) {
        Object[] arrobject = objs;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object obj = arrobject[n2];
            Method[] arrmethod = obj.getClass().getDeclaredMethods();
            int n3 = arrmethod.length;
            int n4 = 0;
            while (n4 < n3) {
                Method m = arrmethod[n4];
                if (m.getParameterCount() == 1 && m.isAnnotationPresent(EventHandler.class) || m.isAnnotationPresent(Listener.class)) {
                    Class<?> eventClass = m.getParameterTypes()[0];
                    if (!this.registry.containsKey(eventClass)) {
                        this.registry.put((Class<? extends Event>) eventClass, new CopyOnWriteArrayList());
                    }
                    this.registry.get(eventClass).add(new Handler(m, obj, m.getDeclaredAnnotation(EventHandler.class).priority()));
                    this.registry.get(eventClass).sort(this.comparator);
                }
                ++n4;
            }
            ++n2;
        }
    }

    public /* varargs */ void unregister(Object ... objs) {
        Object[] arrobject = objs;
        int n = arrobject.length;
        int n2 = 0;
        while (n2 < n) {
            Object obj = arrobject[n2];
            for (List<Handler> list : this.registry.values()) {
                for (Handler data : list) {
                    if (data.parent != obj) continue;
                    list.remove(data);
                }
            }
            ++n2;
        }
    }

    public static <E extends Event> E call(E event) {
        boolean whiteListedEvents = event instanceof EventTick || event instanceof EventPreUpdate || event instanceof EventPostUpdate;
        List<Handler> list = registry.get(event.getClass());
        if (list != null && !list.isEmpty()) {
            for (Handler data : list) {
                try {
                    if (list instanceof Module) {
                        if (((Module)((Object)list)).isEnabled()) {
                            if (whiteListedEvents) {
                                Helper.mc.mcProfiler.startSection(((Module)((Object)list)).getName());
                            }
                            if (whiteListedEvents) {
                                Helper.mc.mcProfiler.endSection();
                            }
                        }
                    } else {
                        if (whiteListedEvents) {
                            Helper.mc.mcProfiler.startSection("non module");
                        }
                        if (whiteListedEvents) {
                            Helper.mc.mcProfiler.endSection();
                        }
                    }
                    data.handler.invokeExact(data.parent, event);
                }
                catch (Throwable e1) {
                    e1.printStackTrace();
                }
            }
        }
        return event;
    }

    private class Handler {
        private MethodHandle handler;
        private Object parent;
        private byte priority;

        public Handler(Method method, Object parent, byte priority) {
            if (!method.isAccessible()) {
                method.setAccessible(true);
            }
            MethodHandle m = null;
            try {
                m = EventBus.this.lookup.unreflect(method);
            }
            catch (IllegalAccessException e) {
                e.printStackTrace();
            }
            if (m != null) {
                this.handler = m.asType(m.type().changeParameterType(0, Object.class).changeParameterType(1, Event.class));
            }
            this.parent = parent;
            this.priority = priority;
        }
    }

}

