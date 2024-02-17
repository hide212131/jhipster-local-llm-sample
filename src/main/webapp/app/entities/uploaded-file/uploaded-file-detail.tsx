import React, { useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { Button, Row, Col } from 'reactstrap';
import { openFile, byteSize } from 'react-jhipster';
import { FontAwesomeIcon } from '@fortawesome/react-fontawesome';

import { useAppDispatch, useAppSelector } from 'app/config/store';

import { getEntity } from './uploaded-file.reducer';

export const UploadedFileDetail = () => {
  const dispatch = useAppDispatch();

  const { id } = useParams<'id'>();

  useEffect(() => {
    dispatch(getEntity(id));
  }, []);

  const uploadedFileEntity = useAppSelector(state => state.uploadedFile.entity);
  return (
    <Row>
      <Col md="8">
        <h2 data-cy="uploadedFileDetailsHeading">Uploaded File</h2>
        <dl className="jh-entity-details">
          <dt>
            <span id="id">ID</span>
          </dt>
          <dd>{uploadedFileEntity.id}</dd>
          <dt>
            <span id="fileId">File Id</span>
          </dt>
          <dd>{uploadedFileEntity.fileId}</dd>
          <dt>
            <span id="filename">Filename</span>
          </dt>
          <dd>{uploadedFileEntity.filename}</dd>
          <dt>
            <span id="data">Data</span>
          </dt>
          <dd>
            {uploadedFileEntity.data ? (
              <div>
                {uploadedFileEntity.dataContentType ? (
                  <a onClick={openFile(uploadedFileEntity.dataContentType, uploadedFileEntity.data)}>Open&nbsp;</a>
                ) : null}
                <span>
                  {uploadedFileEntity.dataContentType}, {byteSize(uploadedFileEntity.data)}
                </span>
              </div>
            ) : null}
          </dd>
        </dl>
        <Button tag={Link} to="/uploaded-file" replace color="info" data-cy="entityDetailsBackButton">
          <FontAwesomeIcon icon="arrow-left" /> <span className="d-none d-md-inline">Back</span>
        </Button>
        &nbsp;
        <Button tag={Link} to={`/uploaded-file/${uploadedFileEntity.id}/edit`} replace color="primary">
          <FontAwesomeIcon icon="pencil-alt" /> <span className="d-none d-md-inline">Edit</span>
        </Button>
      </Col>
    </Row>
  );
};

export default UploadedFileDetail;
