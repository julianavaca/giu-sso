package com.giu.converter;

import com.giu.domain.ServiceComposite;
import com.giu.domain.ServiceItem;
import com.turner.sp.acq.domain.MnuMenuJwfVO;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by biandra on 15/09/15.
 */
@Component
public class MenuConverter {

    public static final String ROOT = "ROOT";

    public List<ServiceComposite> convert(List<MnuMenuJwfVO> allItems){
        List<ServiceComposite> result = new ArrayList<>();
        List<MnuMenuJwfVO> dependenceItemRoot = getSubItems(ROOT, allItems);
        for (MnuMenuJwfVO item : dependenceItemRoot){
            result.add(buildCompositeItem(convertToCompositeItem(item), getSubItems(item.getItmCodigo(), allItems), allItems));
        }
        //result.sort((i1, i2) -> Long.compare(i1.getNivel(), i2.getNivel()));
        return result;
    }

    private ServiceComposite convertToCompositeItem(MnuMenuJwfVO item){
        ServiceComposite serviceComposite = new ServiceComposite();
        serviceComposite.setName(item.getItmDescripcion());
        serviceComposite.setContext(item.getItmCodigo());
        serviceComposite.setIcon(item.getItmGrafico());
        return serviceComposite;
    }

    private  ServiceItem convertToServiceItem(MnuMenuJwfVO item){
        ServiceItem serviceItem = new ServiceItem();
        serviceItem.setName(item.getItmDescripcion());
        serviceItem.setContext(item.getItmCodigo());
        serviceItem.setIcon(item.getItmGrafico());
        serviceItem.setPath(item.getItmPath());
        serviceItem.setPathCommand(item.getIxcComando());
        return serviceItem;
    }

    private List<MnuMenuJwfVO> getSubItems(String itemName, List<MnuMenuJwfVO> mnuMenuJwfVOs){
        List<MnuMenuJwfVO> result = new ArrayList<>();
        for (MnuMenuJwfVO item : mnuMenuJwfVOs ){
            if (item.getItmCodigoDepende().compareToIgnoreCase(itemName) == 0){
                result.add(item);
            }
        }
        //result.sort((i1, i2) -> Long.compare(i1.getNivel(), i2.getNivel()));
        return result;
    }

    private ServiceComposite buildCompositeItem(ServiceComposite composite, List<MnuMenuJwfVO> dependenceItems, List<MnuMenuJwfVO> allItems){
        for(MnuMenuJwfVO item : dependenceItems){
            List<MnuMenuJwfVO> subItems = getSubItems(item.getItmCodigo(), allItems);
            if (isComposite(subItems)){
                composite.add(convertToServiceItem(item));
            }
            else{
                composite.add(buildCompositeItem(convertToCompositeItem(item), subItems, allItems));
            }
        }
        return composite;
    }

    private boolean isComposite(List<MnuMenuJwfVO> subItems) {
        return subItems.isEmpty();
    }

}
