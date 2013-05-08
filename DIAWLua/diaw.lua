--[[

 This program is free software; you can redistribute it and/or modify
 it under the terms of the GNU General Public License as published by
 the Free Software Foundation; either version 2 of the License, or
 (at your option) any later version.

 This program is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 GNU General Public License for more details.

 You should have received a copy of the GNU General Public License
 along with this program; if not, write to the Free Software
 Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston MA 02110-1301, USA.
--]]


-- Global variables
dlg = nil     -- Dialog
title = nil   -- Text input widget
message = nil -- Label
list = nil    -- List widget
okay = nil    -- Okay button
html = nil    -- HTML box
spin = nil    -- spinning icon
films = {}

-- Extension description
function descriptor()
    return { title = "DIAW" ;
             version = "1.0" ;
             author = "Stephane" ;
             url = '' ;
             shortdesc = "have you ever wanted to know which episode of your TV show you have already seen?" ;
             description = "<center><b>have you ever wanted to know which episode of your TV show you have already seen?</b></center>" ;
             capabilities = { "input-listener", "meta-listener" }}
end

function read_meta()
	local command = "\"C:\\Program Files\\Java\\jre7\\bin\\java\" -jar E:\\Code\\DIAWRelease\\Diaw.jar -episode " .. get_URI()
	vlc.msg.dbg("[DIAW] executing " .. command .. "...")
	os.execute( command )
end

function get_URI()
    local item = vlc.item or vlc.input.item()
    if not item then
        return ""
    end
    local filename = string.gsub(item:uri(), "^(.+)%.%w+$", "%1")
    return trim(filename or item:uri())
end

-- Get clean title from filename
function get_title()
    local item = vlc.item or vlc.input.item()
    if not item then
        return ""
    end
    local metas = item:metas()
    if metas["title"] then
        return metas["title"]
    else
        local filename = string.gsub(item:name(), "^(.+)%.%w+$", "%1")
        return trim(filename or item:name())
    end
end

-- Remove leading and trailing spaces
function trim(str)
    if not str then return "" end
    return string.gsub(str, "^%s*(.-)%s*$", "%1")
end