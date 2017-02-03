package cofh.asm;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import net.minecraft.launchwrapper.Launch;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import net.minecraftforge.fml.common.discovery.ASMDataTable;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.relauncher.FMLInjectionData;
import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions ({ "cofh.asm." })
@IFMLLoadingPlugin.SortingIndex (1001)
@IFMLLoadingPlugin.Name ("CoFH Loading Plugin")
public class LoadingPlugin implements IFMLLoadingPlugin {

	public static final String MC_VERSION = "[1.10.2]";
	public static boolean runtimeDeobfEnabled = false;
	public static ASMDataTable ASM_DATA = null;

	public static final String currentMcVersion;
	public static final File minecraftDir;
	public static final boolean obfuscated;

	static {

		boolean obf = true;
		try {
			obf = Launch.classLoader.getClassBytes("net.minecraft.world.World") == null;
		} catch (IOException ignored) {
		}
		obfuscated = obf;
		currentMcVersion = (String) FMLInjectionData.data()[4];
		minecraftDir = (File) FMLInjectionData.data()[6];
	}

	@Override
	public String getAccessTransformerClass() {

		return "";
	}

	@Override
	public String[] getASMTransformerClass() {

		return new String[0];
	}

	@Override
	public String getModContainerClass() {

		return CoFHDummyContainer.class.getName();
	}

	@Override
	public String getSetupClass() {

		return "";
	}

	@Override
	public void injectData(Map<String, Object> data) {

		runtimeDeobfEnabled = (Boolean) data.get("runtimeDeobfuscationEnabled");
		if (data.containsKey("coremodLocation")) {
			myLocation = (File) data.get("coremodLocation");
		}
	}

	public File myLocation;

	public static class CoFHDummyContainer extends DummyModContainer {

		public CoFHDummyContainer() {

			super(new ModMetadata());
			ModMetadata md = getMetadata();
			md.autogenerated = true;
			md.modId = "<CoFH ASM>";
			md.name = md.description = "CoFH ASM";
			md.parent = "CoFHCore";
			md.version = "000";
		}

		@Override
		public boolean registerBus(EventBus bus, LoadController controller) {

			bus.register(this);
			return true;
		}

		@Subscribe
		public void construction(FMLConstructionEvent evt) {

			ASM_DATA = evt.getASMHarvestedData();
			CoFHClassTransformer.scrapeData(ASM_DATA);
		}
	}

}
