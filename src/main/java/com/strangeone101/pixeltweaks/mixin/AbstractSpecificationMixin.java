package com.strangeone101.pixeltweaks.mixin;

import com.pixelmonmod.api.AbstractSpecification;
import com.pixelmonmod.api.Specification;
import com.pixelmonmod.api.requirement.Requirement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(AbstractSpecification.class)
public interface AbstractSpecificationMixin<A, B> extends Specification<A, B> {

    @Accessor(value = "requirements", remap = false)
    public List<Requirement<A, B, ?>> getRequirements();
}
