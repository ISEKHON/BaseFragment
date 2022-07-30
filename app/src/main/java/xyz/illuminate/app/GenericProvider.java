package xyz.illuminate.app;

public interface GenericProvider<F, T> {
    T provide(F obj);
}
