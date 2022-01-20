package com.company;

import java.util.Comparator;

class marksCompare implements Comparator<Player>
{
    @Override
    public int compare(Player s1, Player s2)
    {
        return s2.score - s1.score;
    }

    @Override
    public Comparator<Player> reversed() {
        return null;
    }
}
