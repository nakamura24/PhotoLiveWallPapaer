package jp.livewallpapaer.photo;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;

import jp.template.LiveWallPaper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Environment;
import android.util.Log;
import android.view.MotionEvent;

public class PhotoLiveWallPaper extends LiveWallPaper {
	private static String Tag ="LiveWallPaper";
	private ArrayList<String> mImageList = new ArrayList<String>();
	private int mOffset = 0;

	public PhotoLiveWallPaper() {
		getImageList();
		Collections.sort(mImageList);
	}
	
	@Override
	public void onCreate() {
		super.onCreate();
	}

	private void getImageList() {
		Log.i(Tag, "getImageList");
		try{
			// SDカードのFileを取得
			File file = Environment.getExternalStorageDirectory();
			mImageList.clear();
			getImageSubDir(file.getAbsolutePath() + "/DCIM");
			Log.d(Tag, "getImageList - " + String.valueOf(mImageList.size()));
		} catch (Exception e) {
			Log.e(Tag, e.getMessage());
		}
	}
	private void getImageSubDir(String subdir) {
		Log.i(Tag, "getImageSubDir - " + subdir);
		try {
			File subDir = new File(subdir);
			String subFileName[] = subDir.list();
			int n = 0;
			while(subFileName.length > n){
				File subFile = new File(subDir + "/" + subFileName[n]);
				if(subFile.isDirectory()){
					getImageSubDir(subDir + "/" + subFileName[n]);
				}else if(subFile.getName().endsWith("jpg") || subFile.getName().endsWith("JPG")){
					if(!subDir.getAbsolutePath().contains("/.")) {
						Log.d(Tag, subDir.getPath() + "/" + subFileName[n]);
						mImageList.add(subDir.getPath() + "/" + subFileName[n]);
					}
				}
				n++;
			}
		} catch (Exception e) {
			Log.e(Tag, e.getMessage());
		}
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
	}

	@Override
	public Engine onCreateEngine() {
		return new LiveEngine();
	}

	// 表示状態変更時に呼び出される
	@Override
	public void VisibilityChanged(boolean visible){
		if(visible){
			getImageList();
			Collections.sort(mImageList);
		}
	}

	// オフセット変更時に呼び出される
	@Override
	public boolean OffsetsChanged(float xOffset, float yOffset, float xStep, float yStep, int xPixels, int yPixels){
		return false;
	}

	// キャンバスに描画を行う
	@Override
	public void DrawCanvas(Canvas canvas) {
		// 背景色を設定
		canvas.drawColor(BackgroundColor);
		if(Image.getWidth() < Image.getHeight()) {
			// 画像のリサイズ
			float scale_x = (float)Image.getWidth() / (float)WidthPixels;
			float scale_y = (float)Image.getHeight() / (float)HeightPixels;
			float scale = Math.max(scale_x, scale_y);
			int new_x = (int)(Image.getWidth() / scale);
			int new_y = (int)(Image.getHeight() / scale);
			Image = Bitmap.createScaledBitmap(Image, new_x, new_y, false);
		} else {
			// 画像のリサイズ
			float scale_x = (float)Image.getWidth() / (float)HeightPixels;
			float scale_y = (float)Image.getHeight() / (float)WidthPixels;
			float scale = Math.max(scale_x, scale_y);
			int new_x = (int)(Image.getWidth() / scale);
			int new_y = (int)(Image.getHeight() / scale);
			Image = Bitmap.createScaledBitmap(Image, new_x, new_y, false);
			Matrix matrix = new Matrix();
			matrix.postRotate(90);
			Image = Bitmap.createBitmap(Image, 0, 0, new_x, new_y, matrix, true);
		}
		// キャンバスに画像を描画
		canvas.drawBitmap(Image, 0, 0, null);
	}

	// 描画する画像を変更
	@Override
	public void ChangeImage() {
		// スーパークラスの処理は、ダブルタップした回数に合わせて画像を変更する
		//super.ChangeImage();
		Image = BitmapFactory.decodeFile(mImageList.get(mOffset));
	}

	// 再度描画が行われる前に呼び出される
	@Override
	public void DrawDelay() {
		// スーパークラスの処理は、何もしない
		super.DrawDelay();
	}

	// ダブルタップした時の処理
	@Override
	public boolean DoubleTap(MotionEvent event) {
		mOffset++;
		mOffset %= mImageList.size();
		return true;
	}
	// フリックした時の処理
	@Override
	public boolean Fling(MotionEvent event1, MotionEvent event2, float velocityX, float velocityY) {
		if(mImageList.size() > 0) {
			mOffset += mImageList.size();
			if(velocityX > 0) {
				mOffset--;
				mOffset %= mImageList.size();
			} else if(velocityX < 0) {
				mOffset++;
				mOffset %= mImageList.size();
			}
		}
		return true;
	}
	// 長押しした時の処理
	@Override
	public boolean LongPress(MotionEvent event) {
		// スーパークラスの処理は、何もしない
		return super.LongPress(event);
	}
	// スクロールした時の処理
	@Override
	public boolean Scroll(MotionEvent event1, MotionEvent event2, float distanceX, float distanceY) {
		// スーパークラスの処理は、何もしない
		return super.Scroll(event1, event2, distanceX, distanceY);
	}
	// シングルタップした時の処理
	@Override
	public boolean SingleTapConfirmed(MotionEvent event) {
		// スーパークラスの処理は、何もしない
		return super.SingleTapConfirmed(event);
	}
}
