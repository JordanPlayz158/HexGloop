package com.samsthenerd.hexgloop.casting.gloopifact;

import at.petrak.hexcasting.api.casting.eval.CastingEnvironment;
import at.petrak.hexcasting.api.casting.eval.vm.CastingVM;
import java.util.Set;

// ok so maybe this was a little bit stupid since action already exposes ravenmind and i just missed that,, but it's fine, it's good to have this maybe
public interface ICADHarnessStorage {
    public void addHarness(CastingVM harness);

    // get the harness that has this context
    public CastingVM getHarness(CastingEnvironment ctx);

    public Set<CastingVM> getHarnesses();

    public void removeHarness(CastingVM harness);
}
