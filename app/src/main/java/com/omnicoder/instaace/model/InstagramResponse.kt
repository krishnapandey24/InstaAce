package com.omnicoder.instaace.model


data class Response(val graphql: GraphQL)

data class GraphQL(val shortcode_media: ShortCodeMedia)
//
//data class ShortCodeMedia(@Json(name = "__typename") val media_type: String, val display_resources: List<DisplayResources>,
//                          var owner:Owner, var edge_media_to_caption: EdgeMediaToCaption?, val video_url:String, val edge_sidecar_to_children: EdgeSideCarToChildren)
data class ShortCodeMedia(val display_resources: List<DisplayResources>,
                          var owner:Owner, var edge_media_to_caption: EdgeMediaToCaption?, val video_url:String, val edge_sidecar_to_children: EdgeSideCarToChildren)

data class DisplayResources(val src: String)

data class Owner(val username: String, val full_name: String, val profile_pic_url: String)

data class EdgeMediaToCaption(val edges: Edges)

data class Edges(val node: Node)

data class Node(var text:String)

data class EdgeSideCarToChildren(val edges: List<Edges2>)

data class Edges2(val node: ShortCodeMedia)

