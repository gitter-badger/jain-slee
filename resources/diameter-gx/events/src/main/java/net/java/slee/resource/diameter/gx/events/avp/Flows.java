/*
 * JBoss, Home of Professional Open Source
 *
 * Copyright 2010, Red Hat Middleware LLC, and individual contributors
 * as indicated by the @authors tag. All rights reserved.
 * See the copyright.txt in the distribution for a full listing
 * of individual contributors.
 *
 * This copyrighted material is made available to anyone wishing to use,
 * modify, copy, or redistribute it subject to the terms and conditions
 * of the GNU General Public License, v. 2.0.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License,
 * v. 2.0 along with this distribution; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston,
 * MA 02110-1301, USA.
 */
package net.java.slee.resource.diameter.gx.events.avp;

import net.java.slee.resource.diameter.base.events.avp.GroupedAvp;

/**
 * Defines an interface representing the TFT-Packet-Filter-Information grouped AVP type.<br>
 * <br>
 * Charging rule provisioning over Gx interface(3GPP TS 29.210 V6.7.0) specification:
 * <pre>
 *  5.2.19 TFT-Packet-Filter-Information AVP
    The TFT-Packet-Filter-Information AVP (AVP code 1013) is of type Grouped, and it contains
 *  the information from a single TFT packet filter including the evaluation precedence,
 *  the filter and the Type-of-Service/Traffic Class sent from the TPF to the CRF.
 *  The TPF shall include one TFT-Packet-Filter-Information AVP for each TFT packet filters
 *  applicable at a PDP context in separate TFT-Packet-Filter-Information AVPs within each
 *  charging rule request. corresponding to that PDP context. TFT-Packet-Filter-Information AVPs
 *  are derived from the Traffic Flow Template (TFT) defined in 3GPP TS 24.008 [14].
 *  When SBLP is used the packet filters shall be omitted.
 *  AVP Format:
 *  TFT-Packet-Filter-Information ::= < AVP Header: 1013>
 *                                   [ Precedence ]
 *                                   [ TFT-Filter ]
 *                                   [ ToS-Traffic-Class ]
 * </pre>
 *
 * @author <a href="mailto:karthikeyan_s@spanservices.com"> Karthikeyan Shanmugam (EmblaCom)</a>
 */
public interface Flows extends GroupedAvp {

    long getMediaComponentNumber();

    long getFlowNumber();
    
    boolean hasMediaComponentNumber();
    
    boolean hasFlowNumber();
    
    void setMediaComponentNumber(long mediaComponentNumber);
    
    void setFlowNumber(long flowNumber);

}
