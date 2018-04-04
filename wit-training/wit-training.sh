#!/usr/bin/env bash

# Simple bash script to update Wit.ai project

# Authorization token for Wit project
TOKEN="MMGURXBKQ3YVKYMGDUJQ2K3CKBNMNEVS"

# Update the expressions for coin-flip
curl -XPOST https://api.wit.ai/entities/intent/values?v=20180402 \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "@coin-flip.json"

# Update the expressions for num-galateans
curl -XPOST https://api.wit.ai/entities/intent/values?v=20180402 \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "@num-galateans.json"

# Update the expressions for rec-reading
curl -XPOST https://api.wit.ai/entities/intent/values?v=20180402 \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "@rec-reading.json"

# Update the expressions for movie-quote
curl -XPOST https://api.wit.ai/entities/intent/values?v=20180402 \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "@movie-quote.json"

# Update the expressions for derp
curl -XPOST https://api.wit.ai/entities/intent/values?v=20180402 \
        -H "Authorization: Bearer $TOKEN" \
        -H "Content-Type: application/json" \
        -d "@derp.json"
