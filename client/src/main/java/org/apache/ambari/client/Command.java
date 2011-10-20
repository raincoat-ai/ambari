/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.apache.ambari.client;

import java.io.File;
import java.util.Hashtable;

import javax.ws.rs.core.MediaType;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.apache.ambari.common.rest.entities.Blueprint;
import org.apache.ambari.common.rest.entities.BlueprintInformation;
import org.apache.ambari.common.rest.entities.ClusterDefinition;
import org.apache.ambari.common.rest.entities.ClusterInformation;
import org.apache.ambari.common.rest.entities.Node;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;
import org.apache.commons.cli.Options;
import org.codehaus.jettison.json.JSONObject;

import com.sun.jersey.api.client.ClientResponse;
import com.sun.jersey.api.client.WebResource;

public abstract class Command {
    
    protected String baseURLString = "http://localhost:4080/rest";
    
    //Hashtable<String, String> commandOptions = new Hashtable<String, String>();
    
    //Options options = null;
    
    public Command() {
        
        /* boolean, name, description, required, argname,    
        Option wait = "boolean, false, wait, Optionally wait for cluster to reach desired state";
        String dry_run = "boolean, false dry_run, Dry run";
        String help = "boolean, false, help, help";
        String name = "false, true, name, cluster_name, true, Name of the cluster to be created";
        
        
        
        OptionBuilder.withArgName("cluster_name");
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription( "Name of the cluster to be created");
        Option name = OptionBuilder.create( "name" );
        
        OptionBuilder.withArgName("blueprint_name");
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription( "Name of the cluster blueprint");
        Option blueprint = OptionBuilder.create( "blueprint" );
        
        OptionBuilder.withArgName( "\"node_exp1; node_exp2; ...\"" );
        OptionBuilder.isRequired();
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(  "List of node range expressions separated by semicolon (;) and contained in double quotes (\"\")" );
        Option nodes = OptionBuilder.create( "nodes" );
        
        OptionBuilder.withArgName( "blueprint_revision" );
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(  "Blueprint revision, if not specified latest revision is used" );
        Option blueprint_revision = OptionBuilder.create( "revision" );
        
        OptionBuilder.withArgName( "description" );
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(  "Description to be associated with cluster" );
        Option desc = OptionBuilder.create( "desc" );
        
        OptionBuilder.withArgName( "goalstate" );
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(  "Desired goal state of the cluster" );
        Option goalstate = OptionBuilder.create( "goalstate" );
        
        OptionBuilder.withArgName( "\"component-1; component-2; ...\"" );
        OptionBuilder.hasArg();
        OptionBuilder.withDescription(  "List of components to be active in the cluster. Components are seperated by semicolon \";\"" );
        Option services = OptionBuilder.create( "services" );
        
        OptionBuilder.withArgName( "rolename=\"node_exp1; node_exp2; ... \"" );
        OptionBuilder.hasArgs(2);
        OptionBuilder.withValueSeparator();
        OptionBuilder.withDescription( "Provide node range expressions for a given rolename separated by semicolon (;) and contained in double quotes (\"\")" );
        Option role = OptionBuilder.create( "role" );
    
        this.options = new Options();
        options.addOption( wait );   
        options.addOption(dry_run);
        options.addOption( name );
        options.addOption( blueprint );   
        options.addOption(blueprint_revision);
        options.addOption( desc );
        options.addOption( role );
        options.addOption( goalstate );
        options.addOption( nodes );
        options.addOption( services );
        options.addOption(help);
        */
    }
    
    public void printClusterDefinition(ClusterDefinition def) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(org.apache.ambari.common.rest.entities.ClusterDefinition.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(def, System.out);
    }
    
    public void printClusterInformation(ClusterInformation clsInfo) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(org.apache.ambari.common.rest.entities.ClusterInformation.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(clsInfo, System.out);
    }
    
    public void printNodeInformation(Node node) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(org.apache.ambari.common.rest.entities.Node.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        m.marshal(node, System.out);
    }
    
    public void printBlueprint(Blueprint blueprint, String file_path) throws Exception {
        JAXBContext jc = JAXBContext.newInstance(org.apache.ambari.common.rest.entities.Blueprint.class);
        Marshaller m = jc.createMarshaller();
        m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE);
        if (file_path == null) {
            m.marshal(blueprint, System.out);
        } else {
            m.marshal(blueprint, new File(file_path));
        }
    }
    
    /*
     * TODO: Return Blueprint objects instead of BlueprintInformation???
     */
    public void printBlueprintInformation (WebResource service, BlueprintInformation bpInfo, boolean tree) {
        
        System.out.println("\nName:["+bpInfo.getName()+"], Revision:["+bpInfo.getRevision()+"]");
        
        if (tree) {
            String tab = "    ";
            while (bpInfo.getParentName() != null) {    
                System.out.println(tab+":-> Name:["+bpInfo.getParentName()+"], Revision:["+bpInfo.getParentRevision()+"]");
                ClientResponse response = service.path("blueprints/"+bpInfo.getParentName())
                        .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
                if (response.getStatus() != 404 && response.getStatus() != 200) { 
                    System.err.println("Blueprint list command failed. Reason [Code: <"+response.getStatus()+">, Message: <"+response.getHeaders().getFirst("ErrorMessage")+">]");
                    System.exit(-1);
                }
                if (response.getStatus() == 404) {
                    System.exit(0);
                }
                /* 
                 * Retrieve the blueprint from the response
                 * TODO: 
                 */
                Blueprint bp = response.getEntity(Blueprint.class);
                bpInfo.setName(bp.getName());
                bpInfo.setParentName(bp.getParentName());
                bpInfo.setRevision(bp.getRevision());
                bpInfo.setParentRevision(bp.getParentRevision());
                tab = tab+"        ";
            }
        } 
    }
    
    public void printBlueprintInformation (WebResource service, Blueprint bp, boolean tree) {
        System.out.println("\nName:["+bp.getName()+"], Revision:["+bp.getRevision()+"]");

        if (tree) {
            String tab = "    ";
            while (bp.getParentName() != null) {
                
                System.out.println(tab+":-> Name:["+bp.getParentName()+"], Revision:["+bp.getParentRevision()+"]");
                ClientResponse response = service.path("blueprints/"+bp.getParentName())
                        .accept(MediaType.APPLICATION_JSON).type(MediaType.APPLICATION_JSON).get(ClientResponse.class);
                if (response.getStatus() != 404 && response.getStatus() != 200) { 
                    System.err.println("Blueprint list command failed. Reason [Code: <"+response.getStatus()+">, Message: <"+response.getHeaders().getFirst("ErrorMessage")+">]");
                    System.exit(-1);
                }
                if (response.getStatus() == 404) {
                    System.exit(0);
                }
                /* 
                 * Retrieve the blueprint from the response
                 */
                bp = response.getEntity(Blueprint.class);
                tab = tab+"        ";
            }
        }
    }
    
    public abstract void run () throws Exception;
}
