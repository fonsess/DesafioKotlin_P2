package br.unisanta.teste.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import br.unisanta.teste.R
import br.unisanta.teste.model.Pedido

class PedidoAdapter(private val pedidos: List<Pedido>) :
    RecyclerView.Adapter<PedidoAdapter.AgendamentoViewHolder>() {

    inner class AgendamentoViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val tvComida: TextView = itemView.findViewById(R.id.tvComida)
        val tvBebida: TextView = itemView.findViewById(R.id.tvBebida)
        val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AgendamentoViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_agendamento, parent, false)
        return AgendamentoViewHolder(view)
    }

    override fun onBindViewHolder(holder: AgendamentoViewHolder, position: Int) {
        val agendamento = pedidos[position]
        holder.tvComida.text = agendamento.comida
        holder.tvBebida.text = agendamento.bebida
        holder.tvStatus.text = agendamento.status
    }

    override fun getItemCount(): Int {
        return pedidos.size
    }
}