package com.catfish.dbunit;

import com.github.springtestdbunit.dataset.DataSetModifier;
import org.dbunit.dataset.IDataSet;

import java.lang.reflect.Constructor;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by lcy on 17/6/19.
 */
public class MyDataSetModifiers  implements DataSetModifier{

    private final List<DataSetModifier> modifiers = new ArrayList<DataSetModifier>();

    public IDataSet modify(IDataSet dataSet) {
        for (DataSetModifier modifier : this.modifiers) {
            dataSet = modifier.modify(dataSet);
        }
        return dataSet;
    }

    public void add(Object testInstance, Class<? extends DataSetModifier> modifierClass) {
        try {
            Class<?> enclosingClass = modifierClass.getEnclosingClass();
            if ((enclosingClass == null) || Modifier.isStatic(modifierClass.getModifiers())) {
                add(modifierClass.getDeclaredConstructor());
            } else {
                add(modifierClass.getDeclaredConstructor(enclosingClass), testInstance);
            }
        } catch (Exception ex) {
            throw new IllegalStateException(ex);
        }
    }

    private void add(Constructor<? extends DataSetModifier> constructor, Object... args) throws Exception {
        constructor.setAccessible(true);
        this.modifiers.add(constructor.newInstance(args));
    }

}
