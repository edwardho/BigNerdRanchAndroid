package android.bignerdranch.criminalintent;

import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;

public class DetailPhotoFragment extends DialogFragment {

    private static final String ARG_PHOTO = "photo";
    private ImageView mPhoto;

    public static DetailPhotoFragment newInstance(File imageFile) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_PHOTO, imageFile);

        DetailPhotoFragment fragment = new DetailPhotoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        File photoFile = (File) getArguments().getSerializable(ARG_PHOTO);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.dialog_detail_image, null);
        mPhoto = (ImageView) view.findViewById(R.id.iv_detail_image);

        try{
            if (photoFile == null || !photoFile.exists()) {
                mPhoto.setImageDrawable(null);
            } else {
                Bitmap bitmap = PictureUtils.getScaledBitmap(photoFile.getPath(), getActivity());
                // Handle Camera image rotation on some devices
                ExifInterface ei = new ExifInterface(photoFile.getPath());
                int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                        ExifInterface.ORIENTATION_UNDEFINED);

                Bitmap rotatedBitmap = null;
                switch(orientation) {

                    case ExifInterface.ORIENTATION_ROTATE_90:
                        rotatedBitmap = rotateImage(bitmap, 90);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_180:
                        rotatedBitmap = rotateImage(bitmap, 180);
                        break;

                    case ExifInterface.ORIENTATION_ROTATE_270:
                        rotatedBitmap = rotateImage(bitmap, 270);
                        break;

                    case ExifInterface.ORIENTATION_NORMAL:
                    default:
                        rotatedBitmap = bitmap;
                }

                // Set image on Photo ImageView
                mPhoto.setImageBitmap(rotatedBitmap);
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }

        return new AlertDialog.Builder(getActivity(), R.style.ThemeOverlay_AppCompat_Dialog)
                .setView(view)
                .create();
    }

    public static Bitmap rotateImage(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(),
                matrix, true);
    }
}
