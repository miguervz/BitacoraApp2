package es.studium.bitacoraapp.controlador;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.studium.bitacoraapp.MainActivity;
import es.studium.bitacoraapp.MainActivity2;
import es.studium.bitacoraapp.R;
import es.studium.bitacoraapp.modelos.Apuntes;


public class AdapterApuntes extends RecyclerView.Adapter<AdapterApuntes.ApuntesViewHolder> {


    private List<Apuntes> items;
    private static ItemClickListener listener;

    public AdapterApuntes(MainActivity2 mainActivity, List<Apuntes> items, ItemClickListener listener){

        this.items =items;
        this.listener = listener;
    }

    @Override
    public ApuntesViewHolder onCreateViewHolder( ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.lista_apuntes, viewGroup, false);

        return new ApuntesViewHolder(v);
    }


    @Override
    public void onBindViewHolder(ApuntesViewHolder viewHolder, int i) {
        viewHolder.imagen.setImageResource(items.get(i).getImagen());
        viewHolder.fecha.setText(items.get(i).getFecha());
        viewHolder.texto.setText(items.get(i).getTexto());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


    public static class ApuntesViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{
        // Campos respectivos de item
        public ImageView imagen;
        public TextView fecha;
        public TextView texto;


        public ApuntesViewHolder(@NonNull View v){
            super(v);
            imagen = (ImageView) v.findViewById(R.id.imagen);
            fecha = (TextView) v.findViewById(R.id.fecha);
            texto = (TextView) v.findViewById(R.id.apunte);

            imagen.setOnClickListener(this);


        }

        @Override
        public void onClick(View v) {
            listener.onClick(v, getAdapterPosition());
        }
    }
}