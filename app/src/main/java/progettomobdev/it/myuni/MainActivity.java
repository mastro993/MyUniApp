package progettomobdev.it.myuni;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.ByteArrayOutputStream;
import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener, DashboardFragment.OnFragmentInteractionListener,
        LibrettoFragment.OnFragmentInteractionListener, AppelliFragment.OnFragmentInteractionListener,
        OrarioFragment.OnFragmentInteractionListener, CorsiFragment.OnFragmentInteractionListener {

    private Fragment fragment = null;
    private Intent myIntent;
    private Studente stud;
    private Libretto lib;
    private SharedPreferences prefs;
    private Boolean loaded = false;
    private ImageView userImg;

    private static Context context;

    private static int RESULT_LOAD_IMAGE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        context = MainActivity.this;

        // Shared preferences
        prefs = getSharedPreferences("progettomobdev.it.myuni.Preferenze", getApplicationContext().MODE_PRIVATE);

        // Drawer per navigazione
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        // Inizializzazione
        stud = new Studente();
        lib = new Libretto();

        // Controllo loading studente
        if(utenteEsistente(prefs)) {

            Log.d("INIZIALIZZAZIONE", "Utente esistente, carico i dati");

            // Se non è ancora stato caricato lo carica
            if(!loaded) {
                stud = stud.load(prefs);
                loaded = true;
            }

            setFragment(DashboardFragment.class);

            NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
            navigationView.setNavigationItemSelectedListener(this);

            View hview = navigationView.getHeaderView(0);

            TextView nome = (TextView) hview.findViewById(R.id.menu_nome);
            TextView matricola = (TextView) hview.findViewById(R.id.menu_matricola);
            userImg = (ImageView) hview.findViewById(R.id.imageView);

            nome.setText(stud.getNome() + " " + stud.getCognome());
            matricola.setText(stud.getMatricola().toString());
            if (stud.getImg() != null) {
                String immagine = stud.getImg();
                byte[] imageAsBytes = Base64.decode(immagine.getBytes(), Base64.DEFAULT);
                userImg.setImageBitmap(BitmapFactory.decodeByteArray(imageAsBytes, 0, imageAsBytes.length));
            }

            userImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    changeProfilePicture();
                }
            });
        } else {
            Log.d("INIZIALIZZAZIONE", "Utente non esistente, apertura prima registrazione");
            Intent i = new Intent(MainActivity.this, FirstRegistrationActivity.class);
            startActivity(i);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Inizializzo un oggetto Toolbar dalla view
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

        // Apro fragment/activity in base all'item premuto
        switch (item.getItemId()) {
            case R.id.nav_dashboard:
                setFragment(DashboardFragment.class);
                toolbar.setTitle("Dashboard");
                break;
            case R.id.nav_libretto:
                setFragment(LibrettoFragment.class);
                toolbar.setTitle("Libretto");
                break;
            case R.id.nav_orario:
                setFragment(OrarioFragment.class);
                toolbar.setTitle("Orario");
                break;
            case R.id.nav_corsi:
                setFragment(CorsiFragment.class);
                toolbar.setTitle("Corsi");
                break;
            case R.id.nav_appelli:
                setFragment(AppelliFragment.class);
                toolbar.setTitle("Appelli");
                break;
            case R.id.nav_preferenze:
                launchActivity(PreferenzeActivity.class);
                break;
            case R.id.nav_sync:
                launchActivity(Sincronizzazione.class);
                break;
            case R.id.nav_info:
                launchActivity(InfoActivity.class);
                break;
            default:
                break;
        }

        // Dopo la selezione chiudo il drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);

        return true;
    }

    // Imposta il fragment selezionato nel frame layout
    public void setFragment(Class fragmentClass){
        FragmentTransaction tx = getSupportFragmentManager().beginTransaction();
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        tx.replace(R.id.fragment_container, fragment);
        tx.commit();

    }

    public void launchActivity(Class newActivityClass){
        myIntent = new Intent(MainActivity.this, newActivityClass);
        MainActivity.this.startActivity(myIntent);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    private boolean utenteEsistente(SharedPreferences prefs){
        stud = stud.load(prefs);
        lib = lib.load(prefs);

        if (stud.getNome()== null || lib.getCreditiLaurea() == 0){
            return false;
        }
        else{
            return true;
        }

    }

    private void changeProfilePicture(){
        Intent i = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

        startActivityForResult(i, RESULT_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = {MediaStore.Images.Media.DATA};

            Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            cursor.close();


            Bitmap bmp = null;
            try {

                bmp = getBitmapFromUri(selectedImage);
                Matrix matrix = new Matrix();
                float rotation = rotationForImage(getApplicationContext(), selectedImage);
                if (rotation != 0f) {
                    matrix.preRotate(rotation);
                }
                bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true);

            } catch (IOException e) {
                e.printStackTrace();
            }
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.PNG, 100, baos);
            byte[] b = baos.toByteArray();
            String encoded = Base64.encodeToString(b, Base64.DEFAULT);
            Studente stud = new Studente();
            stud = stud.load(prefs);
            stud.setImg(encoded);
            stud.save(prefs);
            userImg.setImageBitmap(bmp);

        }
    }

    private Bitmap getBitmapFromUri(Uri uri) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 4;
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor, null, options);
        parcelFileDescriptor.close();
        return image;
    }

    public static float rotationForImage(Context context, Uri uri) {
        if (uri.getScheme().equals("content")) {
            String[] projection = { MediaStore.Images.ImageColumns.ORIENTATION };
            Cursor c = context.getContentResolver().query(
                    uri, projection, null, null, null);
            if (c.moveToFirst()) {
                return c.getInt(0);
            }
        } else if (uri.getScheme().equals("file")) {
            try {
                ExifInterface exif = new ExifInterface(uri.getPath());
                int rotation = (int)exifOrientationToDegrees(
                        exif.getAttributeInt(ExifInterface.TAG_ORIENTATION,
                                ExifInterface.ORIENTATION_NORMAL));
                return rotation;
            } catch (IOException e) {
                Log.e("", "Error checking exif");
            }
        }
        return 0f;
    }

    private static float exifOrientationToDegrees(int exifOrientation) {
        if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_90) {
            return 90;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_180) {
            return 180;
        } else if (exifOrientation == ExifInterface.ORIENTATION_ROTATE_270) {
            return 270;
        }
        return 0;
    }
}
