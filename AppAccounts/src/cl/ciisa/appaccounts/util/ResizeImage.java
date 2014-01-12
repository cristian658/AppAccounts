package cl.ciisa.appaccounts.util;



import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.net.Uri;

public class ResizeImage {
	private static Bitmap myBitmap;
	private static Matrix m;
	private static RectF inRect;
	private static RectF outRect;
	private static float[] values;
	private static Bitmap resizedBitmap;
	
	public static Bitmap resizeImage(Uri fileUri){
		myBitmap = BitmapFactory.decodeFile(fileUri.getPath());
		m = new Matrix();
		inRect = new RectF(0, 0, myBitmap.getWidth(), myBitmap.getHeight());
		outRect = new RectF(0, 0, 1000, 1000);
		m.setRectToRect(inRect, outRect, Matrix.ScaleToFit.CENTER);
		values = new float[9];
		m.getValues(values);
		resizedBitmap = Bitmap.createScaledBitmap(myBitmap, (int) (myBitmap.getWidth() * values[0]), (int) (myBitmap.getHeight() * values[4]), true);
		return resizedBitmap;
		
	}

}
