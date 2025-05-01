package org.example.model.plant;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Plant {
    protected String id;
    protected int x;
    protected int y;
    protected int width;
    protected int height;
    protected int reloadTime;
    protected int defenseInitial;
    protected int defense;

}
