package com.example.armap.Helper;

import android.util.Log;

import com.google.sceneform_animation.cq;

import java.util.ArrayList;
import java.util.List;

public class Routing {

    private List<String> path;
    public Routing(){

        path=new ArrayList<>();
    }


    public List<String> Search(String Source, String Destination, List<String> allLabels){

        path.add(Source);

        if(Source.equalsIgnoreCase(Destination)){
            return path;
        }


//    if(precodition(Source,Destination,allLabels)){
//            return null;
//    }
    String sourceCenter=nodeInCenter(Source,allLabels);
    if(sourceCenter!=null){
        path.add(sourceCenter);
        String left=getLeft(sourceCenter);
        String right=getRight(sourceCenter);
        if(left.equalsIgnoreCase(Destination)){
            String center=nodeInCenter(left,allLabels);
            if(center!=null){
                path.add(center);
                path.add(Destination);
            }else {
                path.add(Destination);
            }

        }else if(right.equalsIgnoreCase(Destination)){

            String center=nodeInCenter(right,allLabels);
            if(center!=null){
                path.add(center);
                path.add(Destination);
            }else {
                path.add(Destination);
            }

        }else{
            if(findLeftSubpath(left,Destination,allLabels)){
//            path.add(sourceCenter);

            }else{

                if(findRightSubpath(right,Destination,allLabels)){
//                path.add(sourceCenter);
                }else{
                    path=null;
                }
            }
        }

    }else {
    path=null;
    return path;
    }

    return path;

    }


//    private boolean findSubpath(String source, String destination, List<String> allLabels) {
//
//
//
//        String sourceCenter=nodeInCenter(source,allLabels);
//        if(sourceCenter!=null){
////            path.add(sourceCenter);
//            String left=getLeft(sourceCenter);
//            if(left!=null){
//
//                if(left.equalsIgnoreCase(destination)){
//                    path.add(sourceCenter);
//                    path.add(nodeInCenter(left,allLabels));
//                    Log.d("Searchpath",sourceCenter);
//                    return true;
//                }
//
//                if(!findSubpath(left,destination,allLabels)){
//                    String right=getRight(sourceCenter);
//                    if(right!=null){
//                        if(right.equalsIgnoreCase(destination)){
//                            path.add(nodeInCenter(right,allLabels));
//                            Log.d("Searchpath",sourceCenter);
//                        }
//                        if(findSubpath(right,destination,allLabels)){
//                            path.add(sourceCenter);
//                            return true;
//                        }else {
//                            return  false;
//                        }
//                    }else {
//                        return  false;
//                    }
//
//                }else{
//                    path.add(sourceCenter);
//                    return true;
//                }
//            }else{
//                String right=getRight(sourceCenter);
//                if(right!=null){
//                    if(right.equalsIgnoreCase(destination)){
//                        path.add(nodeInCenter(right,allLabels));
//                        Log.d("Searchpath",sourceCenter);
//                    }
//                    if(findSubpath(right,destination,allLabels)){
//                        path.add(sourceCenter);
//                        return true;
//                    }else {
//                        return  false;
//                    }
//                }else {
//                    return  false;
//                }
//            }
//        }else {
//            return false;
//
//        }
//
//
//
//
//    }


    private boolean findLeftSubpath(String source, String destination, List<String> allLabels) {



        String sourceCenter=nodeInCenter(source,allLabels);
        if(sourceCenter!=null){
//            path.add(sourceCenter);
            String left=getLeft(sourceCenter);
            if(left!=null){

                if(left.equalsIgnoreCase(destination)){
                    path.add(sourceCenter);
                    path.add(nodeInCenter(left,allLabels));
                    Log.d("Searchpath",sourceCenter);
                    return true;
                }

                if(findLeftSubpath(left,destination,allLabels)){
                    path.add(sourceCenter);
                    return true;

                }else{
                    return false;
                }
            }else{
              return false;
            }
        }else {
            return false;

        }




    }

    private boolean findRightSubpath(String source, String destination, List<String> allLabels) {



        String sourceCenter=nodeInCenter(source,allLabels);
        if(sourceCenter!=null){
//            path.add(sourceCenter);
            String right=getRight(sourceCenter);
            if(right!=null){

                if(right.equalsIgnoreCase(destination)){

                    path.add(sourceCenter);
                    String rightCenter=nodeInCenter(right,allLabels);
                    if(rightCenter!=null){
                        path.add(rightCenter);
                    }else {
                        path.add(right);
                    }

                    Log.d("Searchpath",sourceCenter);
                    return true;
                }

                if(findRightSubpath(right,destination,allLabels)){
                    path.add(sourceCenter);
                    return true;

                }else{
                    return false;
                }
            }else{
                return false;
            }
        }else {
            return false;

        }




    }




    public String nodeInCenter(String Label ,List<String> allLabels){
        for (String label : allLabels){
            String tokens[]=label.split("-");
            if(tokens.length>2 && tokens[1].equalsIgnoreCase(Label)){
                return label;
            }

        }
        return null;
    }

    public String getLeft(String Label){
        String tokens[]=Label.split("-");
        if(tokens.length==0){
            return null;
        }

        return tokens[0];

    }

    public String getRight(String Label){
        String tokens[]=Label.split("-");
        if(tokens.length!=3){
            return null;
        }

        return tokens[2];

    }


    public String getCenter(String Label){
        String tokens[]=Label.split("-");
        if(tokens.length!=3){
            return null;
        }

        return tokens[1];

    }

    private boolean precodition(String Source, String Destination, List<String> allLabels) {


        if(Source.equalsIgnoreCase(Destination)){
           return true;
        }
//        String startnode=nodeInCenter(Source,allLabels);
//        String endnode=nodeInCenter(Destination,allLabels);
//
//        if(startnode==null || endnode==null){
//            return true;
//        }



        return false;
    }

  public static  List<String>  findDirections(List<String> path){
        List<String> Direction=new ArrayList<>();

        for (int i=0 ;i<path.size()-1;i++){
            String currentLocation=path.get(i);
           String locationtokens[]=currentLocation.split("-");
            if(locationtokens.length==1){
                Direction.add("Forward");
            }else{
                String nextLocation=path.get(i+1);
                String nextlocationtokens[]=nextLocation.split("-");
                if(nextlocationtokens.length>1){
                    if(locationtokens[0]==nextLocation || locationtokens[0]==nextlocationtokens[1]){
                        Direction.add("Left");
                    }else if(locationtokens[2]==nextLocation || locationtokens[2]==nextlocationtokens[1]){
                        Direction.add("Right");
                    }
                }else{
                    Direction.add("Forward");
                }

            }

        }
        return Direction;
  }

  public  static String nextDirection(String source,String destination) {

      String sourceTokens[] = source.split("-");
      String destTokens[] = destination.split("-");

      if (sourceTokens.length > 2) {
          if (destTokens.length > 2) {
              if (destTokens[1].equalsIgnoreCase(sourceTokens[0])) {
                  return "Left and Forward";
              } else if(destTokens[1].equalsIgnoreCase(sourceTokens[2])){
                  return "Right and Forward";
              }
          } else {
              if (sourceTokens[1].equalsIgnoreCase(destination)) {
                  return "Your Destination is near Look around ";
              } else {
                  return "Look around";
              }
          }
      } else {
          if (destTokens.length > 2) {
              if (destTokens[1].equalsIgnoreCase(source)) {
                  return "Forward";
              } else {
                  return "Look around";
              }
          } else {
              if (source.equalsIgnoreCase(destination)) {
                  return "Reached";
              } else {
                  return "Look around";
              }
          }

      }

      return "Look Around";
  }



}
