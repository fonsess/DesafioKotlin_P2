package br.unisanta.teste.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.teste.R
import br.unisanta.teste.model.Pedido
import br.unisanta.teste.model.pedidoAdmin
import com.google.firebase.firestore.FirebaseFirestore

class StatusAdapter(private val pedidos: List<pedidoAdmin>) :
    RecyclerView.Adapter<StatusAdapter.PedidoViewHolder>() {
    private val db = FirebaseFirestore.getInstance()

    inner class PedidoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvComida: TextView = itemView.findViewById(R.id.tvComida)
        val tvBebida: TextView = itemView.findViewById(R.id.tvBebida)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        val btnAltera: Button = itemView.findViewById(R.id.btnAltera)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PedidoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.pedido_button, parent, false)
        return PedidoViewHolder(view)
    }

    override fun onBindViewHolder(holder: PedidoViewHolder, position: Int) {
        val pedido = pedidos[position]

        holder.tvComida.text = pedido.comida
        holder.tvBebida.text = pedido.bebida
        holder.tvStatus.text = pedido.status

        holder.btnAltera.setOnClickListener {
            if (pedido.status == "pendente") {
                val pedidoRef = db.collection("Restaurante")
                    .document(pedido.codigo)

                pedidoRef.update("status", "em preparo")
                    .addOnSuccessListener {
                        pedido.status = "em preparo"
                        holder.tvStatus.text = pedido.status
                        notifyItemChanged(position)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }else if (pedido.status == "em preparo") {
                val pedidoRef = db.collection("Restaurante")
                    .document(pedido.codigo)

                pedidoRef.update("status", "pronto para entrega")
                    .addOnSuccessListener {
                        pedido.status = "pronto para entrega"
                        holder.tvStatus.text = pedido.status
                        notifyItemChanged(position)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }else if (pedido.status == "pronto para entrega") {
                val pedidoRef = db.collection("Restaurante")
                    .document(pedido.codigo)

                pedidoRef.update("status", "entregue")
                    .addOnSuccessListener {
                        pedido.status = "entregue"
                        holder.tvStatus.text = pedido.status
                        notifyItemChanged(position)
                    }
                    .addOnFailureListener { e ->
                        e.printStackTrace()
                    }
            }
            else{
            }
        }
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }
}

