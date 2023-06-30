package io.endigo.plugins.pdfviewflutter;

import android.content.Context;
import android.net.Uri;
import android.view.View;

import io.flutter.plugin.common.BinaryMessenger;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.platform.PlatformView;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import com.github.barteksc.pdfviewer.PDFView;
import com.github.barteksc.pdfviewer.PDFView.Configurator;

public class FlutterPDFView implements PlatformView, MethodCallHandler {
    private static final String METHOD_CHANNEL = "plugins.endigo.io/pdfview_";
    public static final String FILE_PATH = "filePath";
    public static final String PDF_DATA = "pdfData";
    public static final String ERROR = "error";
    public static final String ON_ERROR = "onError";
    public static final String PAGE = "page";
    public static final String PAGE_COUNT = "pageCount";
    public static final String CURRENT_PAGE = "currentPage";
    public static final String SET_PAGE = "setPage";

    private final PDFView pdfView;
    private final MethodChannel methodChannel;

    @SuppressWarnings("unchecked")
    FlutterPDFView(Context context, BinaryMessenger messenger, int id, Map<String, Object> params) {
        pdfView = new PDFView(context, null);
        methodChannel = new MethodChannel(messenger, METHOD_CHANNEL + id);
        methodChannel.setMethodCallHandler(this);

        Configurator config = null;
        if (params.get(FILE_PATH) != null) {
            String filePath = (String) params.get(FILE_PATH);
            config = pdfView.fromUri(getURI(filePath));
        } else if (params.get(PDF_DATA) != null) {
            byte[] data = (byte[]) params.get(PDF_DATA);
            config = pdfView.fromBytes(data);
        }
        if (config != null) {
            config
                    .enableSwipe(true)
                    .enableAnnotationRendering(true)
                    .enableDoubletap(true)
                    .onError(t -> {
                        Map<String, Object> args = new HashMap<>();
                        args.put(ERROR, t.toString());
                        methodChannel.invokeMethod(ON_ERROR, args);
                    }).load();
        }
    }

    @Override
    public View getView() {
        return pdfView;
    }

    @Override
    public void onMethodCall(MethodCall methodCall, Result result) {
        switch (methodCall.method) {
            case PAGE_COUNT:
                getPageCount(result);
                break;
            case CURRENT_PAGE:
                getCurrentPage(result);
                break;
            case SET_PAGE:
                setPage(methodCall, result);
                break;
            default:
                result.notImplemented();
                break;
        }
    }

    void getPageCount(Result result) {
        result.success(pdfView.getPageCount());
    }

    void getCurrentPage(Result result) {
        result.success(pdfView.getCurrentPage());
    }

    void setPage(MethodCall call, Result result) {
        if (call.argument(PAGE) != null) {
            int page = call.argument(PAGE);
            pdfView.jumpTo(page);
        }

        result.success(true);
    }

    @Override
    public void dispose() {
        methodChannel.setMethodCallHandler(null);
    }

    private Uri getURI(final String uri) {
        Uri parsed = Uri.parse(uri);

        if (parsed.getScheme() == null || parsed.getScheme().isEmpty()) {
            return Uri.fromFile(new File(uri));
        }
        return parsed;
    }
}
