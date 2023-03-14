package com.itzblaze.modulewithasm.asm;

import com.itzblaze.modulewithasm.ProxyServer;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.util.Map;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.Name;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin.SortingIndex;

@SortingIndex(2147473647)
@Name("ProxyMod")
public class FMLLoadingPlugin implements IFMLLoadingPlugin {
    public String[] getASMTransformerClass() {
        return new String[] { ClassTransformer.class.getName() };
    }

    public String getModContainerClass() {
        return ProxyServer.class.getName();
    }

    public String getSetupClass() {
        return null;
    }

    public void injectData(Map<String, Object> data) {}

    public String getAccessTransformerClass() {
        return null;
    }
}
