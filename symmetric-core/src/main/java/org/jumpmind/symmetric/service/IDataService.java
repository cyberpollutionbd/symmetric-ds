/**
 * Licensed to JumpMind Inc under one or more contributor
 * license agreements.  See the NOTICE file distributed
 * with this work for additional information regarding
 * copyright ownership.  JumpMind Inc licenses this file
 * to you under the GNU General Public License, version 3.0 (GPLv3)
 * (the "License"); you may not use this file except in compliance
 * with the License.
 *
 * You should have received a copy of the GNU General Public License,
 * version 3.0 (GPLv3) along with this library; if not, see
 * <http://www.gnu.org/licenses/>.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jumpmind.symmetric.service;

import java.util.Date;
import java.util.List;

import org.jumpmind.db.sql.ISqlReadCursor;
import org.jumpmind.db.sql.ISqlTransaction;
import org.jumpmind.db.sql.Row;
import org.jumpmind.symmetric.ext.IHeartbeatListener;
import org.jumpmind.symmetric.io.data.Batch;
import org.jumpmind.symmetric.model.Data;
import org.jumpmind.symmetric.model.DataEvent;
import org.jumpmind.symmetric.model.DataGap;
import org.jumpmind.symmetric.model.Node;
import org.jumpmind.symmetric.model.OutgoingBatch.Status;
import org.jumpmind.symmetric.model.TableReloadRequest;
import org.jumpmind.symmetric.model.TableReloadRequestKey;
import org.jumpmind.symmetric.model.TriggerHistory;
import org.jumpmind.symmetric.model.TriggerRouter;

/**
 * This service provides an API to access and update {@link Data}.
 */
public interface IDataService {
        
    public void saveTableReloadRequest(TableReloadRequest request);
    
    public TableReloadRequest getTableReloadRequest(TableReloadRequestKey key);
    
    public String reloadNode(String nodeId, boolean reverseLoad, String createBy);
    
    public String reloadTable(String nodeId, String catalogName, String schemaName, String tableName);

    public String reloadTable(String nodeId, String catalogName, String schemaName, String tableName, String overrideInitialLoadSelect);

    /**
     * Sends a SQL command to the remote node for execution by creating a SQL event that is synced like other data
     * 
     *  @param nodeId the remote node where the SQL statement will be executed
     * @param catalogName used to find the sym_trigger entry for table that will be associated with this event 
     * @param schemaName used to find the sym_trigger entry for table that will be associated with this event
     * @param tableName used to find the sym_trigger entry for table that will be associated with this event
     * @param sql the SQL statement to run on the remote node database
     *  @return message string indicating success or error
     */
    public String sendSQL(String nodeId, String catalogName, String schemaName, String tableName, String sql);

    public void insertReloadEvents(Node targetNode, boolean reverse);

    public boolean insertReloadEvent(TableReloadRequest request, boolean deleteAtClient);
    
    public long insertReloadEvent(ISqlTransaction transaction, Node targetNode,
            TriggerRouter triggerRouter, TriggerHistory triggerHistory, String overrideInitialLoadSelect, boolean isLoad, long loadId, String createBy, Status status);
    
    public void sendScript(String nodeId, String script, boolean isLoad);
    
    public boolean sendSchema(String nodeId, String catalogName, String schemaName,
            String tableName, boolean isLoad);

    /**
     * Update {@link Node} information for this node and call {@link IHeartbeatListener}s.
     */
    public void heartbeat(boolean force);

    public void insertHeartbeatEvent(Node node, boolean isReload);
    
    public long insertData(Data data);
    
    public void insertDataEvents(ISqlTransaction transaction, List<DataEvent> events);
    
    public void insertDataAndDataEventAndOutgoingBatch(Data data, String channelId, List<Node> nodes, String routerId, boolean isLoad, long loadId, String createBy);
    
    public long insertDataAndDataEventAndOutgoingBatch(ISqlTransaction transaction, Data data,
            String nodeId, String routerId, boolean isLoad, long loadId, String createBy, Status status);

    public long insertDataAndDataEventAndOutgoingBatch(Data data, String nodeId, String routerId, boolean isLoad, long loadId, String createBy);

    public void insertSqlEvent(ISqlTransaction transaction, Node targetNode, String sql, boolean isLoad, long loadId, String createBy);

    public void insertSqlEvent(Node targetNode, String sql, boolean isLoad, long loadId, String createBy);
    
    public void insertScriptEvent(ISqlTransaction transaction, String channelId,
            Node targetNode, String script, boolean isLoad, long loadId, String createBy);

    public void insertCreateEvent(Node targetNode, TriggerHistory triggerHistory, String routerId, boolean isLoad, long loadId, String createBy);
    
    /**
     * Count the number of data ids in a range
     */
    public int countDataInRange(long firstDataId, long secondDataId);
    
    public void checkForAndUpdateMissingChannelIds(long firstDataId, long lastDataId);

    public List<DataGap> findDataGapsByStatus(DataGap.Status status);
    
    public List<DataGap> findDataGaps();

    public Date findCreateTimeOfEvent(long dataId);
    
    public Date findCreateTimeOfData(long dataId);
    
    public Date findNextCreateTimeOfDataStartingAt(long dataId);

    public Data createData(String catalogName, String schemaName, String tableName);

    public Data createData(String catalogName, String schemaName, String tableName, String whereClause);
    
    public Data createData(ISqlTransaction transaction, String catalogName, String schemaName, String tableName, String whereClause);

    public Data mapData(Row row);
    
    public List<Number> listDataIds(long batchId, String nodeId);
    
    public List<Data> listData(long batchId, String nodeId, long startDataId, String channelId, int maxRowsToRetrieve);
    
    public void updateDataGap(DataGap gap, DataGap.Status status);
    
    public void insertDataGap(DataGap gap);

    public void insertDataGap(ISqlTransaction transaction, DataGap gap);

    public void deleteDataGap(ISqlTransaction transaction, DataGap gap);
    
    public void deleteDataGap(DataGap gap);
    
    public void deleteCapturedConfigChannelData();
    
    public long findMaxDataId();
    
    public ISqlReadCursor<Data> selectDataFor(Batch batch);
    
    public ISqlReadCursor<Data> selectDataFor(Long batchId, String channelId);

}