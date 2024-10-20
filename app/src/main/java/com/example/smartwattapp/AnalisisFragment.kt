import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.smartwattapp.HomeFragment
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import com.example.smartwattapp.R

class AnalisisFragment : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?):
    View? {
        // Infla el layout existente para este fragmento
                return inflater.inflate(R.layout.analisis_panel, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val topBar = view.findViewById<View>(R.id.top_bar)
        topBar.findViewById<View>(R.id.return_icon).setOnClickListener {
            // Se Obtiene el FragmentManager para realizar la navegación
            val fragmentManager = requireActivity().supportFragmentManager
            val destinoFragment = HomeFragment() // Se reemplazó con el fragmento al que deseamos navegar
            fragmentManager.beginTransaction()
                .replace(R.id.fragment_container, destinoFragment)
                .addToBackStack(null)
                .commit()
        }
        // Agregar lógica específica para el fragmento de la vista de analisis_panel.xml
    }
}