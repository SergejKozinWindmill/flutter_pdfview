package io.endigo.plugins.pdfviewflutter;

import androidx.annotation.NonNull;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.plugin.common.BinaryMessenger;

public class PDFViewFlutterPlugin implements FlutterPlugin {

     private static final String PLUGIN_NAME  = "plugins.endigo.io/pdfview";

    @Override
    public void onAttachedToEngine(@NonNull FlutterPluginBinding binding) {
        binding
                .getPlatformViewRegistry()
                .registerViewFactory(PLUGIN_NAME, new PDFViewFactory(binding.getBinaryMessenger()));
    }

    @Override
    public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    }
}
