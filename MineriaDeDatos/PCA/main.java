import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.*;
import java.lang.*;
import java.util.*;
import java.util.List;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import net.sf.javaml.core.Instance;
import net.sf.javaml.core.SparseInstance;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.featureselection.scoring.GainRatio;
import net.sf.javaml.featureselection.ranking.RecursiveFeatureEliminationSVM;
import net.sf.javaml.featureselection.subset.GreedyForwardSelection;
import net.sf.javaml.distance.PearsonCorrelationCoefficient;

public class main
{
	public static void main( String[] args ) throws FileNotFoundException, IOException, InterruptedException
  {
  	if ( args.length != 6 )
    {
    	System.out.println( "Error en la cantidad de argumentos " );
   	}
    else
    {
			BufferedReader br = null;
			PrintWriter pw = null;
			PrintWriter pwInformationGain = null;
			PrintWriter pwForwardSelection = null;
			ArrayList < String > informationGainSelected = new ArrayList < String >( );
			ArrayList < String > forwardSelected = new ArrayList < String > ( ) ;
			ArrayList < String > bolsa = cargarBW( args[ 2 ] );
			ArrayList < String > azar = cargarBW( args[ 3 ] );
      try
      {
      	br = new BufferedReader( new FileReader( new File( args[ 0 ] ) ) ); /* Archivo de entrada que contiene los tuits en formato CSV*/
				Dataset dataSetFeatureSelection = new DefaultDataset();
				String linea = "";
				System.out.println( "Cargando instancias..." );
        while( ( linea = br.readLine( ) ) != null)
        {
					String[] data = linea.split( "\t" );
					if( data.length != 3 ) continue;
					String idTuit = data[ 0 ];
					String etiqueta = data[ 1 ];
					String caracteristicas = data[ 2 ];
					if ( azar.contains( idTuit ) )
					{
						double []vectorInstancia = new double[ bolsa.size( ) ];
						String []features = caracteristicas.split( " " );
						for( int i = 0 ; i < features.length ; i++ )
						{
							String []onlyFeat = features[ i ].split( ":" );
							int position = Integer.parseInt( onlyFeat[ 0 ] ) - 1;
							double value = Double.parseDouble( onlyFeat[ 1 ] );
							vectorInstancia[ position ] = value;
						}
						Instance instance = new DenseInstance( vectorInstancia, etiqueta );
						dataSetFeatureSelection.add( instance );
					}
      	}
        br.close( );

				pwInformationGain = new PrintWriter( new FileWriter( new File( args[ 4 ] ) ) );
        pwForwardSelection = new PrintWriter( new FileWriter( new File( args[ 5 ] ) ) );

				System.out.println( "Entrenando Modelo Information Gain..." );				
				GainRatio gainRatio = new GainRatio( );
				gainRatio.build( dataSetFeatureSelection );
				for ( int i = 0; i < gainRatio.noAttributes( ); i++ )
    		{
					if ( gainRatio.score( i ) > 0 )
					{
						pwInformationGain.println( bolsa.get( i )  + "\t" + gainRatio.score( i ) );
						informationGainSelected.add( bolsa.get( i ) );
					}
				}
				pwInformationGain.close( );			
				/* SVM Recurvise */
				/*
				RecursiveFeatureEliminationSVM svmrfe = new RecursiveFeatureEliminationSVM(0.2);
				svmrfe.build( dataSetFeatureSelection );
				//for (int i = 0; i < svmrfe.noAttributes( ); i++)
		    //	System.out.println( svmrfe.rank( i ) );
			
				/* ForwardSelection */ 
				System.out.println( "Entrenando Modelo Forward Selected..." );
				double cantdouble =  bolsa.size( ) / 5;
				int cantidadSeleccion = ( int ) Math.floor( cantdouble ) ;
				GreedyForwardSelection ga = new GreedyForwardSelection( cantidadSeleccion, new PearsonCorrelationCoefficient());
				ga.build( dataSetFeatureSelection );
				//System.out.println(ga.selectedAttributes());
				Set<Integer> set = ga.selectedAttributes( );
				Iterator<Integer> iterator = set.iterator();
    		while(iterator.hasNext()) 
				{
        	Integer setElement = iterator.next( );
					pwForwardSelection.println( bolsa.get( setElement ) );
					forwardSelected.add( bolsa.get( setElement ) );	
    		}

				pwForwardSelection.close( );
				System.out.println( "Intersectando Modelos..." );
				pw = new PrintWriter( new FileWriter( new File( args[ 1 ] ) ) );
				for( int i = 0 ; i < forwardSelected.size( ) ; i++ )
					if( informationGainSelected.contains( forwardSelected.get( i ) ) )
						pw.println( forwardSelected.get( i ) );
				pw.close( );		
				System.out.println( "Proceso Finalizado" );
     	}
      catch ( Exception e)
      {
      	e.printStackTrace();
      }
   	}
	}
	
	public static ArrayList<String> cargarBW( String archivoInput ) throws FileNotFoundException, IOException
    {
      ArrayList<String> BW = new ArrayList<String>();
      File finput = null;
      FileReader fr = null;
      BufferedReader br = null;
      try
      {
        finput = new File( archivoInput );
        fr = new FileReader ( finput );
        br = br = new BufferedReader(fr);

        String linea = "";

        while( ( linea = br.readLine( ) ) != null)
          BW.add( linea );

        br.close( );
        fr.close( );

      }
      catch ( Exception e )
      {
        e.printStackTrace();
      }

      return BW;
    }

}




