package es.studium.bitacoraapp.controlador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.bitacoraapp.MainActivity;
import es.studium.bitacoraapp.R;
import es.studium.bitacoraapp.modelos.Cuadernos;

public class AdapterCuadernos extends RecyclerView.Adapter<AdapterCuadernos.CuadernosViewHolder> {


    private List<Cuadernos> items;
    private static ItemClickListener listener;

    public AdapterCuadernos(MainActivity mainActivity, List<Cuadernos> items, ItemClickListener listener){

        this.items =items;
        this.listener = listener;
    }

    @Override
    public CuadernosViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lista_cuadernos, viewGroup, false);

        return new CuadernosViewHolder(v);
    }


    @Override
    public void onBindViewHolder(CuadernosViewHolder viewHolder, int i) {
        viewHolder.imagen.setImageResource(items.get(i).getImagen());
        viewHolder.cuaderno.setText(items.get(i).getCuaderno());

    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class CuadernosViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        // Campos respectivos de item
        public ImageView imagen;
        public TextView cuaderno;



        public CuadernosViewHolder(@NonNull View v){
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            cuaderno = (TextView) v.findViewById(R.id.cuaderno);

            imagen.setOnClickListener(this);
            cuaderno.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}