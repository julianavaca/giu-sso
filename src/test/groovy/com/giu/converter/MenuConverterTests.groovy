package com.giu.converter

import com.turner.domain.ServiceComposite
import com.turner.domain.ServiceItem
import com.turner.sp.acq.domain.MnuMenuJwfVO
import spock.lang.Specification

/**
 * Created by biandra on 18/09/15.
 */
class MenuConverterTests extends Specification{

    public static final String LEVEL1 = "LEVEL1"
    public static final String ROOT = "ROOT"
    public static final String GRAPHIC = "GRAPHIC"
    public static final String LEVEL2 = "LEVEL2"
    public static final String LEVEL3 = "LEVEL3"
    public static final String DESCRIPTION = "DESCRIPTION"
    MenuConverter menuConverter = new MenuConverter()

    def "convert"() {
        setup:
        List<MnuMenuJwfVO> allItemsMock = new ArrayList<>()
        MnuMenuJwfVO itemMock1 = new MnuMenuJwfVO()
        itemMock1.setItmCodigo(LEVEL1)
        itemMock1.setItmCodigoDepende(ROOT)
        itemMock1.setItmDescripcion(DESCRIPTION)
        itemMock1.setItmGrafico(GRAPHIC)
        allItemsMock.add(itemMock1)
        MnuMenuJwfVO itemMock2 = new MnuMenuJwfVO()
        itemMock2.setItmCodigo(LEVEL2)
        itemMock2.setItmCodigoDepende(LEVEL1)
        itemMock2.setItmDescripcion(DESCRIPTION)
        itemMock2.setItmGrafico(GRAPHIC)
        allItemsMock.add(itemMock2)
        MnuMenuJwfVO itemMock3 = new MnuMenuJwfVO()
        itemMock3.setItmCodigo(LEVEL3)
        itemMock3.setItmCodigoDepende(LEVEL2)
        itemMock3.setItmDescripcion(DESCRIPTION)
        itemMock3.setItmGrafico(GRAPHIC)
        allItemsMock.add(itemMock3)

        when:
        List<ServiceComposite> serviceComposites = menuConverter.convert(allItemsMock)

        then:
        serviceComposites.get(0) != null
        serviceComposites.get(0).getName() == DESCRIPTION
        serviceComposites.get(0).getContext() == LEVEL1
        serviceComposites.get(0).getIcon() == GRAPHIC
        serviceComposites.get(0) instanceof ServiceComposite
        serviceComposites.get(0).getServices().get(0) != null
        serviceComposites.get(0).getServices().get(0).getName() == DESCRIPTION
        serviceComposites.get(0).getServices().get(0).getContext() == LEVEL2
        serviceComposites.get(0).getServices().get(0).getIcon() == GRAPHIC
        serviceComposites.get(0).getServices().get(0) instanceof ServiceComposite
        ((ServiceComposite)serviceComposites.get(0).getServices().get(0)).getServices().get(0) != null
        ((ServiceComposite)serviceComposites.get(0).getServices().get(0)).getServices().get(0).getName() == DESCRIPTION
        ((ServiceComposite)serviceComposites.get(0).getServices().get(0)).getServices().get(0).getContext() == LEVEL3
        ((ServiceComposite)serviceComposites.get(0).getServices().get(0)).getServices().get(0).getIcon() == GRAPHIC
        ((ServiceComposite)serviceComposites.get(0).getServices().get(0)).getServices().get(0) instanceof ServiceItem
    }
}
