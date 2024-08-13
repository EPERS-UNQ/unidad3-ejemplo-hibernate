package ar.edu.unq.unidad3.service;

import ar.edu.unq.unidad3.modelo.Item;

import java.util.Collection;

public record ItemsPaginados(Collection<Item> items, int total) {}
